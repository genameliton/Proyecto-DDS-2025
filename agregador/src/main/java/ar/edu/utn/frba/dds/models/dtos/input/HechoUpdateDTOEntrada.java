package ar.edu.utn.frba.dds.models.dtos.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class HechoUpdateDTOEntrada {
  @JsonProperty("titulo")
  private String titulo;
  @JsonProperty("descripcion")
  private String descripcion;
  @JsonProperty("categoria")
  private String categoria;
  @JsonProperty("latitud")
  private Double latitud;
  @JsonProperty("longitud")
  private Double longitud;
  @JsonProperty("fecha_hecho")
  @JsonFormat(pattern = "yyyy-MM-dd")  //formateo cadena de texto a fecha
  private LocalDate fechaHecho;
  @JsonProperty("multimedia")
  private List<MultimediaDTOEntrada> multimedia;
}
