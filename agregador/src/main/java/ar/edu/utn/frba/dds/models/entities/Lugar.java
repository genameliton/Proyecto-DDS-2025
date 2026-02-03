package ar.edu.utn.frba.dds.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Lugar {
  @Column
  private String departamento;
  @Column
  private String provincia;
  @Column
  private String municipio;

  public Lugar() {
    this.departamento = "";
    this.provincia = "";
    this.municipio = "";
  }
}
