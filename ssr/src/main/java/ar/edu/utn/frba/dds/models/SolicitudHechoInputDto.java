package ar.edu.utn.frba.dds.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class SolicitudHechoInputDTO {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  @JsonProperty("fecha_hecho")
  private LocalDateTime fechaHecho;
  @JsonProperty("created_at")
  private LocalDateTime createdAt;
  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;
  private List<MultimediaInputDTO> multimedia;
  private String autor;
}
