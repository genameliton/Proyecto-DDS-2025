package ar.edu.utn.frba.dds.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity @Table(name = "hecho")
  public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String titulo;
  @Column(length = 2000)
  private String descripcion;
  @Column
  private String categoria;
  @Column
  private Double latitud;
  @Column
  private Double longitud;
  @Column
  private LocalDateTime fecha_hecho;
  @Column
  private LocalDateTime created_at;

  public Hecho(String titulo, String descripcion, String categoria, Double latitud, Double longitud, LocalDateTime fecha_hecho, LocalDateTime created_at) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fecha_hecho = fecha_hecho;
    this.created_at = created_at;
  }
}
