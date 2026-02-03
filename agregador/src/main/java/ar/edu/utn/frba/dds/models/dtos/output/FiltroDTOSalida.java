package ar.edu.utn.frba.dds.models.dtos.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiltroDTOSalida {
  private String tipoFiltro;
  private String valor;
  private String tipoFuente;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate fechaInicio;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate fechaFin;
}