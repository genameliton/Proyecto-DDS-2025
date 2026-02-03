package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "filtroTitulo")
@NoArgsConstructor
public class FiltroFuente extends IFiltroStrategy {
  @Enumerated(EnumType.STRING)
  @Column
  private TipoFuente tipoFuente;

  public FiltroFuente(String tipo) {
    try {
      tipoFuente = TipoFuente.valueOf(tipo.toUpperCase());
    } catch (Exception e) {
      throw new IllegalArgumentException("Filtro para tipo de fuente " + tipo + " no soportado");
    }
  }

  @Override
  public Boolean cumpleFiltro(Hecho hecho) {
    return hecho.getOrigen().getTipo() == this.tipoFuente;
  }
}
