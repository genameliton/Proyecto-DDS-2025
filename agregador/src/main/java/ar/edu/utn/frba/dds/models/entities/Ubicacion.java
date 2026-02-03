package ar.edu.utn.frba.dds.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//@Embeddable
@Embeddable
public class Ubicacion {
  //@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column
  private Double latitud;
  @Column
  private  Double longitud;
  @Embedded
  private Lugar lugar;

  public Ubicacion() {
  }


  public Boolean mismaUbicacion(Double latitud, Double longitud) {
    return this.latitud.equals(latitud) && this.longitud.equals(longitud);
  }
}