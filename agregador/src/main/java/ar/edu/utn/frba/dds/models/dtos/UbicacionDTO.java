package ar.edu.utn.frba.dds.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UbicacionDTO {
  @JsonProperty("departamento")
  private DepartamentoDTO departamento;
  @JsonProperty("provincia")
  private ProvinciaDTO provincia;
  @JsonProperty("gobierno_local")
  private MunicipioDTO municipio;
}

