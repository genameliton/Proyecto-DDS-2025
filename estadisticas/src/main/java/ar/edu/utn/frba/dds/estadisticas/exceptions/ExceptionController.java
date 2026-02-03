package ar.edu.utn.frba.dds.estadisticas.exceptions;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleNotFoundEntity(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  //no pudo conectar a coleccion
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handlerNotFoundColeccion(RuntimeException ex) {
    log.error("ERROR_502 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
    log.error("ERROR_400 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }
  @ExceptionHandler(EntityExistsException.class)
  public ResponseEntity<String> handlerNotFoundColeccion(EntityExistsException ex) {
    log.error("ERROR_409 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneral(Exception ex) {
    log.error("ERROR_5xx - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
  }
}
