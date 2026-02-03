package ar.edu.utn.frba.dds.models.dtos.output;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HechoOutputDTO {
  private Long id;
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
  private List<MultimediaOutputDTO> multimedia;
  private String autor;
}
