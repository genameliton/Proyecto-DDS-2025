package ar.edu.utn.frba.dds.exceptions;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerController {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleNotFoundEntity(EntityNotFoundException ex) {
    log.error("ERROR_404 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
    log.error("ERROR_400 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }
  //errores de consulta de hechos de fuentes
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException ex){
    log.error("ERROR_502 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
  }

  @ExceptionHandler(EntityExistsException.class)
  public ResponseEntity<String> handleExistingEntity(EntityExistsException ex){
    //log the error
    log.error("ERROR_409 - {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralException(Exception e, HttpServletRequest request) {
    log.error("ERROR_5xx - Ha ocurrido un fallo grave en la ruta: {}",
        request.getRequestURI(),
        e);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado.");
  }
}
