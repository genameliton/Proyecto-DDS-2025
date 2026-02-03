package ar.edu.utn.frba.dds.models;

import lombok.Data;

@Data
public class HechoDTO {
  private Long id;
  private String titulo;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String provincia = "";
  private String municipio = "";
  private String departamento = "";
  private String tipoFuente;
  private String nombreAutor;
}
