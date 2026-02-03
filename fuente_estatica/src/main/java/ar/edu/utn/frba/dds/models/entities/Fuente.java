package ar.edu.utn.frba.dds.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity @Table(name = "fuente")
public class Fuente {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String url;
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "fuente_id", referencedColumnName = "id")
  private List<Hecho> hechos;
}
