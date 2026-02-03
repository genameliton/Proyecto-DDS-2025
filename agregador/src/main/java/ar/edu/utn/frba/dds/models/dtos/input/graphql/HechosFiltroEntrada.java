package ar.edu.utn.frba.dds.models.dtos.input.graphql;

import lombok.Data;
@Data
public class HechosFiltroEntrada {
  private String categoria;
  private String fecha_acontecimiento_desde;
  private String fecha_acontecimiento_hasta;
  private String provincia;
  private String municipio;
  private String departamento;
}