package ar.edu.utn.frba.dds.models.dtos.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultimediaOutputDTO {
  private String nombre;
  private String ruta;
  private String formato;
}
