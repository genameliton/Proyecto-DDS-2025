package ar.edu.utn.frba.dds.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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
  @JsonProperty("fecha_hecho")
  private LocalDateTime fechaHecho;
  @JsonProperty("created_at")
  private LocalDateTime createdAt;
}
