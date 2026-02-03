package ar.edu.utn.frba.dds.models;

import lombok.Data;

@Data
public class ColeccionHechosDTO {
  private String id;
  private HechoPaginadoDTOSalida hechos;
}
