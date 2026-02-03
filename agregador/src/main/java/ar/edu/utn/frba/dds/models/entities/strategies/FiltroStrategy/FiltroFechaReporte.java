package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Entity @DiscriminatorValue("fecha_reporte")
@NoArgsConstructor
public class FiltroFechaReporte extends FiltroFecha {
  public FiltroFechaReporte(LocalDateTime inicio, LocalDateTime fin) {
    super(inicio, fin, Hecho::getFechaCarga, TipoFiltro.FILTRO_FECHA_REPORTE);
  }
}