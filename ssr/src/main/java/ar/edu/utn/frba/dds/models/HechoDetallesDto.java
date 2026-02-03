package ar.edu.utn.frba.dds.models;

import java.util.List;
import lombok.Data;

@Data
public class HechoDetallesDTO {

  private Long id;
  private String titulo;
  private String categoria;
  private String descripcion;
  private Double latitud;
  private Double longitud;
  private String provincia;
  private String municipio;
  private String departamento;  
  private String fechaAcontecimiento;
  private String nombreAutor;
  private List<MultimediaDTO> multimedia;

}
