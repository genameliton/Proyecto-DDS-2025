package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.function.Function;
import lombok.NoArgsConstructor;

@Getter
@Entity @Table(name="filtroFecha")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_filtro_fecha")
@NoArgsConstructor(force = true)
public abstract class FiltroFecha extends IFiltroStrategy {
  @Column
  private LocalDateTime fechaInicio;
  @Column
  private LocalDateTime fechaFinal;
  @Transient
  private final Function<Hecho, LocalDateTime> extractorFecha;

  public FiltroFecha(LocalDateTime fechaInicio, LocalDateTime fechaFinal, Function<Hecho, LocalDateTime> extractorFecha, TipoFiltro tipoFiltro) {
    if (fechaInicio != null && fechaFinal != null && fechaInicio.isAfter(fechaFinal)) {
      throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }
    this.fechaInicio = fechaInicio;
    this.fechaFinal = fechaFinal;
    this.extractorFecha = extractorFecha;
    this.tipoFiltro = tipoFiltro;
  }

  @Override
  public Boolean cumpleFiltro(Hecho hecho) {
    LocalDateTime fecha = extractorFecha.apply(hecho);

    if (fechaInicio != null && fechaFinal != null) {
      return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFinal);
    } else if (fechaInicio != null) {
      return !fecha.isBefore(fechaInicio);
    } else if (fechaFinal != null) {
      return !fecha.isAfter(fechaFinal);
    } else {
      return true;
    }
  }

  public void setFechaInicio(LocalDateTime fechaInicio) {
    if (fechaFinal != null && fechaInicio != null && fechaInicio.isAfter(fechaFinal)) {
      throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }
    this.fechaInicio = fechaInicio;
  }

  public void setFechaFinal(LocalDateTime fechaFinal) {
    if (fechaInicio != null && fechaFinal != null && fechaFinal.isBefore(fechaInicio)) {
      throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
    }
    this.fechaFinal = fechaFinal;
  }
}
