package ar.edu.utn.frba.dds.services;

import static org.springframework.http.HttpStatus.CONFLICT;
import ar.edu.utn.frba.dds.models.EstadisticaDTO;
import ar.edu.utn.frba.dds.models.NuevaEstadisticaDTO;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class EstadisticaService {
  private final WebApiCallerService webApiCallerService;
  private final String estadisticasServiceUrl;

  public EstadisticaService(
      WebApiCallerService webApiCallerService,
      @Value("${estadisticas.service.url}") String estadisticasServiceUrl) {
    this.webApiCallerService = webApiCallerService;
    this.estadisticasServiceUrl = estadisticasServiceUrl;

  }

  public void crearEstadistica(NuevaEstadisticaDTO request) {
    try {
      webApiCallerService.post(estadisticasServiceUrl, request, Void.class);
    } catch (RuntimeException e) {
      if (e.getMessage().contains("Error de conexión")) {
        throw new RuntimeException("No se pudo conectar con el servicio externo. Por favor, intentá más tarde.");
      }

      if (e.getCause() instanceof WebClientResponseException wcre) {
        if (wcre.getStatusCode().equals(CONFLICT)) {
          throw new IllegalArgumentException(
              "Ya existe una estadística sobre la coleccion con la categoria ingresada.");
        }
      }

      throw new RuntimeException("Error al crear estadística: " + e.getMessage());
    }
  }

  public List<EstadisticaDTO> obtenerEstadisticas() {
    return this.webApiCallerService.getList(estadisticasServiceUrl, EstadisticaDTO.class);
  }
}
