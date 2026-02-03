package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.Formato;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity @Table(name = "multimedia")
public class Multimedia {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String nombre;

  @Column
  private String ruta;

  @Column
  private Formato formato;

  @ManyToOne
  @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  private Hecho hecho;
  public Multimedia() {

  }
}
