package ar.edu.utn.frba.dds.models.dtos;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class FuenteNuevoDTO {
  public String tipoFuente;
  public String url;
}
