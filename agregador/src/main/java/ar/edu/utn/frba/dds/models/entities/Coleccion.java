package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.EstadoColeccion;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "coleccion")
public class Coleccion {
  @Id
  private String id;
  @Column
  private String titulo;
  @Column
  private String descripcion;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado")
  private EstadoColeccion estado;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "coleccion_id", referencedColumnName = "id")
  private Set<IFiltroStrategy> criterios = new HashSet<>();

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
  @JoinTable(name = "coleccion_fuente", joinColumns = @JoinColumn(name = "coleccion_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "fuente_id", referencedColumnName = "id"))
  private Set<Fuente> fuentes;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "algoritmo_id", referencedColumnName = "id")
  private IConsensoStrategy algoritmoConsenso;

  public Coleccion() {
    this.id = UUID.randomUUID().toString();
    this.fuentes = new HashSet<>();
    this.estado = EstadoColeccion.PROCESANDO;
  }

  public Set<Hecho> getHechos() {
    Set<Hecho> hechos = new HashSet<>();
    fuentes.stream()
        .forEach(fuente -> hechos.addAll(fuente.getHechos()));

    if (!criterios.isEmpty()) {
      return hechos.stream().filter(h -> h.cumpleFiltros(criterios)).collect(Collectors.toSet());
    } else {
      return hechos;
    }
  }

  public void refrescarHechosCurados() {
    if (algoritmoConsenso != null) {
      algoritmoConsenso.actualizarHechos(this.getHechos(), fuentes);
    }
  }

  public Set<Hecho> getHechosCurados() {
    if (algoritmoConsenso != null) {
      return algoritmoConsenso.getHechosCurados();
    } else {
      return new HashSet<>();
    }
  }

  public void addCriterio(IFiltroStrategy filtro) {
    this.criterios.add(filtro);
  }

  public void addFuente(Fuente fuente) {
    this.fuentes.add(fuente);
  }

  public void removeFuente(String idFuente) {
    fuentes.removeIf(fuente -> EqualsBuilder.reflectionEquals(fuente.getId(), idFuente));
  }

  public void clearFuentes() {
    this.fuentes.clear();
  }

  public void setearFuentes(Set<Fuente> fuentes) {
    this.fuentes.clear();
    this.fuentes.addAll(fuentes);
  }

  public void setearCriterios(Set<IFiltroStrategy> filtros) {
    this.criterios.clear();
    this.criterios.addAll(filtros);
  }

  public void clearCriterios() {
    this.criterios.clear();
  }
}
