package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import java.util.Optional;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Lugar;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "filtroMunicipio")
@NoArgsConstructor
public class FiltroMunicipio extends IFiltroStrategy {
  @Column
  private String municipio;

  public FiltroMunicipio(String municipio) {
    if (municipio.isBlank()) {
      throw new IllegalArgumentException("Municipio no puede ser nulo");
    }
    this.tipoFiltro = TipoFiltro.FILTRO_MUNICIPIO;
    this.municipio = municipio;
  }

  @Override
  public Boolean cumpleFiltro(Hecho hecho) {
    String municipioHecho = Optional.ofNullable(hecho.getUbicacion())
        .map(Ubicacion::getLugar)
        .map(Lugar::getMunicipio)
        .orElse(null);
    if (municipioHecho != null)
      return municipioHecho.toLowerCase().contains(municipio.toLowerCase());
    else
      return false;
  }
}
