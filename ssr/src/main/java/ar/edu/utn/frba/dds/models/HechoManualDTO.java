package ar.edu.utn.frba.dds.models;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

@Data
public class HechoManualDTO {
  private String titulo;
  private String descripcion;
  private String categoria;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaAcontecimiento;
  private Double latitud;
  private Double longitud;
  private String autor;
}
