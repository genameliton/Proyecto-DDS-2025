package ar.edu.utn.frba.dds.models.dtos.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Data;

@Data
public class FiltroDTOEntrada {
  private String tipoFiltro;
  private String valor; // para título, categoria, provincia, municipio o departamento
  private String tipoFuente; // para filtro de fuente
  @JsonFormat(pattern = "yyyy-MM-dd") // formateo cadena de texto a fecha
  private LocalDate fechaInicio; // para filtros de fecha
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate fechaFin;
}
