package ar.edu.utn.frba.dds.estadisticas.schedulers;

import ar.edu.utn.frba.dds.estadisticas.services.IEstadisticasService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EstadisticasScheduler {
  private IEstadisticasService estadisticasService;
  public EstadisticasScheduler(IEstadisticasService estadisticasService) {
    this.estadisticasService = estadisticasService;
  }

  @Scheduled(fixedRate = 7200000) // 2 horas
  public void actualizarEstadisticas() {
    estadisticasService.actualizarEstadisticas();
  }

  @Scheduled(fixedRate = 14400000)
  public void eliminarEstadisticasNoVigentes() {
    estadisticasService.eliminarEstadisticasNoVigentes();
  }
}
