package ar.edu.utn.frba.dds.estadisticas.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity @Table(name = "detalles_estadistica")
public class DetalleEstadistica {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String categoriaMayoresHechos;
  @Column
  private String provinciaMayorCantHechos;
  @Column
  private String provinciaMayorCantHechosCategoria;
  @Column
  private Number horaMayorCantHechos;
  @Column
  private Integer solicitudesSpam;
}
