package ar.edu.utn.frba.dds.models;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SolicitudEliminacionDetallesDTO {
  private Long id;
  private String titulo;
  private String motivo;
  private LocalDateTime fecha;
  private String estadoActual;
  private Integer esSpam;
  private Long idHecho;
  private String creador;
}
