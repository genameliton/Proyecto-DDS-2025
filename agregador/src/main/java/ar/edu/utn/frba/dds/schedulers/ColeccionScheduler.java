package ar.edu.utn.frba.dds.schedulers;

import ar.edu.utn.frba.dds.services.ColeccionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ColeccionScheduler {
  private final ColeccionService coleccionService;

  public ColeccionScheduler(ColeccionService coleccionService) {
    this.coleccionService = coleccionService;
  }

  @Scheduled(fixedDelay = 3600000, initialDelay = 3600000)
  public void refrescarColecciones() {
    coleccionService.refrescoFuentes();
  }

  @Scheduled(cron = "${scheduler.cron.curaduria}", zone = "America/Argentina/Buenos_Aires")
  public void refrescarHechosCurados() {
    coleccionService.refrescarHechosCurados();
  }

  @Scheduled(fixedRate = 300000)
  public void tareaDeRescateColecciones() {
    coleccionService.procesarColeccionesPendientes();
  }
}
