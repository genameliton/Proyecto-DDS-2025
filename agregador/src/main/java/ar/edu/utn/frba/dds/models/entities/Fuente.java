package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.utils.HechoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.reactive.function.client.WebClient;

@Entity
@Getter
@Setter
@NoArgsConstructor
// ELIMINADAS: @Inheritance y @DiscriminatorColumn porque ya no usas subclases
public class Fuente {
  @Id
  protected String id = UUID.randomUUID().toString();

  @Column
  protected String url;

  @Enumerated(EnumType.STRING)
  @Column
  protected TipoFuente tipoFuente;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "fuente_hecho", joinColumns = @JoinColumn(name = "fuente_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id"))
  protected Set<Hecho> hechos = new HashSet<>();

  public Fuente(String url, TipoFuente tipoFuente) {
    this.url = url;
    this.tipoFuente = tipoFuente;
  }

  public Set<Hecho> obtenerHechosRefrescados(HechoConverter hechoConverter, WebClient webClient) {
    try {
      Set<Hecho> hechosExternos = webClient.get()
          .uri(url + "/hechos")
          .retrieve()
          .bodyToFlux(HechoDTOEntrada.class)
          .map(hecho -> hechoConverter.fromDTO(hecho, tipoFuente))
          .collect(Collectors.toSet())
          .block();

      return hechosExternos;

    } catch (Exception e) {
      throw new RuntimeException("Error al tratar de obtener hechos de la fuente " + this.url, e);
    }
  }

  public Set<Hecho> getHechos() {
    return hechos.stream()
        .sorted(Comparator.comparing(Hecho::getId))
        .collect(Collectors.toSet());
  }

  public void addHechos(Set<Hecho> hechos) {
    this.hechos.addAll(hechos);
  }

  public void addHecho(Hecho hecho) {
    this.hechos.add(hecho);
  }

  public void removeHecho(Hecho h) {
    this.hechos.remove(h);
  }
}