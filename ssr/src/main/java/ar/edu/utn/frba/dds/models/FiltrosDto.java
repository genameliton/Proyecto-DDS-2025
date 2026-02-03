package ar.edu.utn.frba.dds.models;

import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class FiltrosDTO {
  String curados;
  String categoria;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  LocalDate fecha_acontecimiento_desde;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  LocalDate fecha_acontecimiento_hasta;
  String provincia;
  String municipio;
  String departamento;
}
