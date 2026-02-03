package ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "algoritmo_consenso")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class IConsensoStrategy {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Column
  protected Integer cantidadMinimaApariciones = 0;

  public abstract TipoAlgoritmo getTipo();

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "hecho_consensuado", joinColumns = @JoinColumn(name = "algoritmo_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id"))
  private Set<Hecho> hechosConsensuados = new HashSet<>();

  protected Boolean cumpleConsensoBase(Hecho hecho, Set<Fuente> fuentes, Integer cantMin) {
    cantidadMinimaApariciones = cantMin;

    long apariciones = fuentes.stream()
        .map(Fuente::getHechos)
        .filter(hechosDeFuente -> hechosDeFuente.stream()
            .anyMatch(h -> sonHechosIguales(h, hecho)))
        .count();

    return apariciones >= cantMin;
  }

  public abstract Boolean cumpleConsenso(Hecho hecho, Set<Fuente> fuentes);

  public void actualizarHechos(Set<Hecho> hechos, Set<Fuente> fuentes) {
    this.hechosConsensuados.clear();
    Set<Hecho> hechosC = hechos.stream()
        .filter(h -> cumpleConsenso(h, fuentes))
        .collect(Collectors.toSet());

    this.hechosConsensuados.addAll(hechosC);
  }

  public Set<Hecho> getHechosCurados() {
    return this.hechosConsensuados;
  }

  private Boolean sonHechosIguales(Hecho h1, Hecho h2) {
    if (h1 == null || h2 == null)
      return false;

    Boolean titulosIguales = h1.getTitulo() != null && h1.getTitulo().equals(h2.getTitulo());
    Boolean categoriasIguales = h1.getCategoria() != null && h1.getCategoria().equals(h2.getCategoria());
    Boolean fechasIguales = h1.getFechaAcontecimiento() != null &&
        h1.getFechaAcontecimiento().equals(h2.getFechaAcontecimiento());
    Boolean ubicacionesIguales = ubicacionIgual(h1, h2);
    return titulosIguales && categoriasIguales && fechasIguales && ubicacionesIguales;
  }

  private Boolean ubicacionIgual(Hecho h1, Hecho h2) {
    if (h1.getUbicacion() == null || h2.getUbicacion() == null)
      return false;

    return Objects.equals(h1.getUbicacion().getLatitud(), h2.getUbicacion().getLatitud()) &&
        Objects.equals(h1.getUbicacion().getLongitud(), h2.getUbicacion().getLongitud());
  }
}