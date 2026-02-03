package ar.edu.utn.frba.dds.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ColeccionCreadaDTO {
  @JsonProperty("id")
  private String id;
  @JsonProperty("titulo")
  private String titulo;
  @JsonProperty("descripcion")
  private String descripcion;
}
