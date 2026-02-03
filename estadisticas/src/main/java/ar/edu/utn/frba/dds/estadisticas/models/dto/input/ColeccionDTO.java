package ar.edu.utn.frba.dds.estadisticas.models.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ColeccionDTO {
  @JsonProperty("id")
  private String id;
  @JsonProperty("titulo")
  private String titulo;
  @JsonProperty("descripcion")
  private String descripcion;
  @JsonProperty("cantSolicitudesSpam")
  private Integer cantSolicitudesSpam;
}