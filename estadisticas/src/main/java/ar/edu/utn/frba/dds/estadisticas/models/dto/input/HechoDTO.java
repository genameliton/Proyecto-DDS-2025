package ar.edu.utn.frba.dds.estadisticas.models.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HechoDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("titulo")
  private String titulo;
  @JsonProperty("categoria")
  private String categoria;
  @JsonProperty("latitud")
  private Double latitud;
  @JsonProperty("longitud")
  private Double longitud;
  @JsonProperty("provincia")
  private String provincia;
  @JsonProperty("municipio")
  private String municipio;
  @JsonProperty("departamento")
  private String departamento;
  @JsonProperty("fechaAcontecimiento")
  private LocalDateTime fechaAcontecimiento;
}
