package ar.edu.utn.frba.dds.controllers;

import jakarta.persistence.EntityNotFoundException;
import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerController {

  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
    Map<String, Object> errorBody = new HashMap<>();
    errorBody.put("mensaje", ex.getMessage());
    errorBody.put("estado", HttpStatus.BAD_REQUEST.value());
    errorBody.put("timestamp", ZonedDateTime.now());
    log.error("ERROR_FILE_NOT_FOUND - {}", ex.getMessage());
    return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpClientErrorException.NotFound.class)
  public ResponseEntity<?> notFoundExceptionHandler(Exception e) {
    log.error("ERROR_404 - {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(e.getMessage());
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleNotFoundEntity(EntityNotFoundException ex) {
    log.error("ERROR_404 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneral(Exception ex) {
    log.error("ERROR_5xx - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
  }
}
