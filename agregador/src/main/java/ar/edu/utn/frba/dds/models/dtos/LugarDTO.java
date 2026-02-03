package ar.edu.utn.frba.dds.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LugarDTO {
  @JsonProperty("ubicacion")
  private UbicacionDTO ubicacion;
}
