package ar.edu.utn.frba.dds.models;

import lombok.Data;

@Data
public class HechoPaginacionDTO {
  private Long id;
  private String titulo;
  private String categoria;
  private String provincia;
  private String municipio;
  private String departamento;
  private String fechaAcontecimiento;
  private Double latitud;
  private Double longitud;

}
