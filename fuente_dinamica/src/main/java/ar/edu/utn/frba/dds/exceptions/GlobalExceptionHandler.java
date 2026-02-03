package ar.edu.utn.frba.dds.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ContrasenaIncorrectaException.class)
  public ResponseEntity<String> handleContrasenaIncorrecta(ContrasenaIncorrectaException ex) {
    log.error("ERROR_401 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
    log.error("ERROR_400 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneral(Exception ex) {
    log.error("ERROR_5xx - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
  }

  @ExceptionHandler(RecursoNoEncontradoException.class)
  public ResponseEntity<String> handleNotFound(RecursoNoEncontradoException ex) {
    log.error("ERROR_404 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }
}
