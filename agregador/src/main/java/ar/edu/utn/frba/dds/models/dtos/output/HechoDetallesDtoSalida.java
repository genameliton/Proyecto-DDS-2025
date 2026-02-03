package ar.edu.utn.frba.dds.models.dtos.output;

import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class HechoDetallesDTOSalida {
  private Long id;
  private String titulo;
  private String descripcion;
  private Double latitud;
  private Double longitud;
  private String categoria;
  private String provincia;
  private String municipio;
  private String departamento;
  private TipoFuente tipoOrigen;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fechaCarga;
  private List<MultimediaDTOSalida> multimedia;
  private String nombreAutor;
}
