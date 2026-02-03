package ar.edu.utn.frba.dds.models;

import lombok.Data;

@Data
public class EstadisticaDTO {
  private Long id;
  private String urlColeccion;
  private String nombre;
  private String categoriaEspecifica;
  private DetalleEstadisticaDTO detalle;
  private int vigente = 1;
}
