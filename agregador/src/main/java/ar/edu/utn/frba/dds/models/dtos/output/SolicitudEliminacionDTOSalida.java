package ar.edu.utn.frba.dds.models.dtos.output;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SolicitudEliminacionDTOSalida {
  private Long id;
  private String titulo;
  private LocalDateTime fecha;
  private String estadoActual;
  private Integer esSpam;
}
