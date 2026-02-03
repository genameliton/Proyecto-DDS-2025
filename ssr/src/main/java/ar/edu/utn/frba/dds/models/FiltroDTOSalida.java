package ar.edu.utn.frba.dds.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Data;

@Data
public class FiltroDTOSalida {
  private String tipoFiltro;
  private String valor;
  private String tipoFuente;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate fechaInicio;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate fechaFin;
}
