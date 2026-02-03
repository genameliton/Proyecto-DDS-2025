package ar.edu.utn.frba.dds.estadisticas.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity @Table(name="estadistica")
public class Estadistica {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String urlColeccion;
  @Column
  private String nombre;
  @Column
  private String categoriaEspecifica;
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "estadistica_id", referencedColumnName = "id")
  private DetalleEstadistica detalle;
  @Column
  private int vigente = 1;
}
