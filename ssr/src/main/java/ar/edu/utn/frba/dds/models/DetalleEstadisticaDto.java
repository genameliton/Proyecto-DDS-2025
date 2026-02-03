package ar.edu.utn.frba.dds.models;

import lombok.Data;

@Data
public class DetalleEstadisticaDTO {
  private Long id;
  private String categoriaMayoresHechos;
  private String provinciaMayorCantHechos;
  private String provinciaMayorCantHechosCategoria;
  private Number horaMayorCantHechos;
  private Integer solicitudesSpam;
}
