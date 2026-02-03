package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "hecho")

public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "id_externo")
  private Long idExterno;
  @Column
  private String titulo;
  @Column(length = 3000)
  private String descripcion;
  @Column
  private String categoria;
  @Embedded
  private Ubicacion ubicacion;
  @Column
  private LocalDateTime fechaAcontecimiento;
  @Column
  private LocalDateTime fechaCarga;
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "origen_id", referencedColumnName = "id")
  private Origen origen;

  @OneToMany(mappedBy = "hecho", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<Multimedia> multimedia;

  @ManyToMany(mappedBy = "hechos", fetch = FetchType.LAZY)
  private Set<Fuente> fuentes;

  @ManyToMany(mappedBy = "hechosConsensuados", fetch = FetchType.LAZY)
  private Set<IConsensoStrategy> consensos;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Hecho hecho)) {
      return false;
    }
    return titulo.equals(hecho.titulo);
  }

  public boolean cumpleFiltros(Set<IFiltroStrategy> filtros) {
    return filtros == null || filtros.isEmpty() || filtros.stream().allMatch(f -> f.cumpleFiltro(this));
  }
}
