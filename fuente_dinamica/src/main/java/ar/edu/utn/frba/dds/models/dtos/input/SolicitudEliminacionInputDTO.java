package ar.edu.utn.frba.dds.models.dtos.input;

import lombok.Data;

@Data
public class SolicitudEliminacionInputDTO {
  private Long hechoId;
  private String titulo;
  private String texto; //Mínimo 500 caracteres
  private String responsable;
}
