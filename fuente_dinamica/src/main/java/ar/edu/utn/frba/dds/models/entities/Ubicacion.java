package ar.edu.utn.frba.dds.models.entities;

import jakarta.persistence.*;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ubicacion")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ubicacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "ubicacion")
  private List<Hecho> hechosAsociados;

  @Column(name = "latitud")
  private Double latitud;

  @Column(name = "longitud")
  private Double longitud;

  public Ubicacion(Double latitud, Double longitud) {
    this.latitud = latitud;
    this.longitud = longitud;
  }
}
