package ar.edu.utn.frba.dds.models;

import com.opencsv.bean.CsvBindByName;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.opencsv.bean.CsvDate;
import lombok.Getter;
import lombok.Setter;

@Getter
public class HechoCsv {
  @CsvBindByName(column = "Título")
  private String titulo;

  @CsvBindByName(column = "Descripción")
  private String descripcion;

  @CsvBindByName(column = "Categoría")
  private String categoria;

  @CsvBindByName(column = "Latitud")
  private Double latitud;

  @CsvBindByName(column = "Longitud")
  private Double longitud;

  @CsvBindByName(column = "Fecha del hecho")
  @CsvDate("dd/MM/yyyy")
  private LocalDate fecha;
}

