package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Lugar;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "filtroDepartamento")
@NoArgsConstructor
public class FiltroDepartamento extends IFiltroStrategy {
  @Column
  private String departamento;

  public FiltroDepartamento(String departamento) {
    if (departamento.isBlank()) {
      throw new IllegalArgumentException("Departamento no puede ser nulo");
    }
    this.departamento = departamento;
    this.tipoFiltro = TipoFiltro.FILTRO_DEPARTAMENTO;
  }

  @Override
  public Boolean cumpleFiltro(Hecho hecho) {
    String departamentoHecho = Optional.ofNullable(hecho.getUbicacion())
        .map(Ubicacion::getLugar)
        .map(Lugar::getDepartamento)
        .orElse(null);
    if (departamentoHecho != null)
      return departamentoHecho.toLowerCase().contains(departamento.toLowerCase());
    else
      return false;
  }
}
