package ar.edu.utn.frba.dds.models.dtos.external.api.hecho;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HechoDTO {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDateTime fecha_hecho;
  private LocalDateTime created_at;
  //private LocalDateTime updated_at;
}
