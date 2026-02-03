package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "filtroCategoria")
@NoArgsConstructor
public class FiltroCategoria extends IFiltroStrategy {
  @Column
  private String nombreCategoria;

  public FiltroCategoria(String nombreCategoria) {
    if (nombreCategoria.isBlank()){
      throw new IllegalArgumentException("categoria no puede ser nula");
    }
    this.nombreCategoria = nombreCategoria;
    this.tipoFiltro = TipoFiltro.FILTRO_CATEGORIA;
  }
  @Override
  public Boolean cumpleFiltro(Hecho hecho) {
    return hecho.getCategoria().toLowerCase().contains(nombreCategoria.toLowerCase());
  }
}
