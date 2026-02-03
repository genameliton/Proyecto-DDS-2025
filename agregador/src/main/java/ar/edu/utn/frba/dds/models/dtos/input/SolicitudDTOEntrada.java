package ar.edu.utn.frba.dds.models.dtos.input;

import lombok.Data;

@Data
public class SolicitudDTOEntrada {
  private Long idHecho;
  private String titulo;
  private String texto;
  private String creador;
}
