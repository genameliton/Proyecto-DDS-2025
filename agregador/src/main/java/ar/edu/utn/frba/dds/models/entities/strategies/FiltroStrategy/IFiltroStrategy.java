package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity @Table(name="filtro")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class IFiltroStrategy {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Enumerated(EnumType.STRING)
  @Column
  protected TipoFiltro tipoFiltro;
  public Boolean cumpleFiltro(Hecho hecho) {
    return false;
  }
}


