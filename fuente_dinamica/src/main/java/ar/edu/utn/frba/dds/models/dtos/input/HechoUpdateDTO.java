package ar.edu.utn.frba.dds.models.dtos.input;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class HechoUpdateDTO {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
}
