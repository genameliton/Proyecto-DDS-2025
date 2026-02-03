package ar.edu.utn.frba.dds.models;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SolicitudHechoDTO {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDateTime fecha_acontecimiento;
  private LocalDateTime fecha_carga;
  private String estado_hecho;
  private String motivo_rechazo;
  private String sugerencias;
  private LocalDateTime fecha_revision;
  private String revisado_por;
  private String autor;
}