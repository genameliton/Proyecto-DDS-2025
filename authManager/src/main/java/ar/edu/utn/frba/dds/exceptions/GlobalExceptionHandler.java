package ar.edu.utn.frba.dds.exceptions;

import jakarta.persistence.EntityExistsException;
import java.security.SignatureException;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
    log.error("ERROR_400 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<String> handleUsernameNotFound(UsernameNotFoundException ex) {
    log.error("ERROR_404 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(EntityExistsException.class)
  public ResponseEntity<Map<String, String>> handleDuplicatedUsername(EntityExistsException ex) {
    log.error("ERROR_409 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
    log.error("ERROR_5xx - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","Error inesperado"));
  }
}
