package ar.edu.utn.frba.dds.models;

import lombok.Data;

@Data
public class SolicitudEliminacionDTO {
  private Long idHecho;
  private String titulo;
  private String texto;
  private String creador;
}
