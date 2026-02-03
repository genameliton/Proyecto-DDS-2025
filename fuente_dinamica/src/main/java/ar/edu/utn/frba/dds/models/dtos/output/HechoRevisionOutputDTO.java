package ar.edu.utn.frba.dds.models.dtos.output;

import ar.edu.utn.frba.dds.models.enums.EstadoHecho;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HechoRevisionOutputDTO {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDateTime fecha_acontecimiento;
  private LocalDateTime fecha_carga;
  private EstadoHecho estado_hecho;
  private String motivo_rechazo;
  private String sugerencias;
  private LocalDateTime fecha_revision;
  private String revisado_por;
}
