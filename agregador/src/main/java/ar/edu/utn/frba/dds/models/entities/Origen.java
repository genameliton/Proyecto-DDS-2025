package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "origen")
public class Origen {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Getter @Setter @Enumerated(EnumType.STRING) @Column
  private TipoFuente tipo;
  @Getter
  @Setter
  @Column
  private String autor;

  public Origen() {
    this.autor = "";
  }
}
