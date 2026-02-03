package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.Coleccion;
import ar.edu.utn.frba.dds.models.ColeccionHechosDTO;
import ar.edu.utn.frba.dds.models.ColeccionNuevaDTO;

import ar.edu.utn.frba.dds.models.FiltrosDTO;
import ar.edu.utn.frba.dds.models.HechoDetallesDTO;
import ar.edu.utn.frba.dds.models.ResumenActividadDTO;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionDetallesDTO;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionDTO;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionPaginadaDTO;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AgregadorService {
  private final WebApiCallerService webApiCallerService;
  private final RestTemplate restTemplate;
  private final HttpGraphQlClient gqlAgregadorClient;
  private final String agregadorServiceUrl;

  public AgregadorService(
      WebApiCallerService webApiCallerService,
      RestTemplate restTemplate,
      HttpGraphQlClient gqlAgregadorClient,
      @Value("${agregador.service.url}") String agregadorServiceUrl) {
    this.webApiCallerService = webApiCallerService;
    this.restTemplate = restTemplate;
    this.gqlAgregadorClient = gqlAgregadorClient;
    this.agregadorServiceUrl = agregadorServiceUrl;
  }

  public List<Coleccion> obtenerColecciones() {
    return gqlAgregadorClient.documentName("getColecciones")
        .retrieve("colecciones")
        .toEntityList(Coleccion.class).block();
  }

  public ColeccionHechosDTO getHechosColeccion(String idColeccion, FiltrosDTO filtros, int page) {
    return gqlAgregadorClient.documentName("getHechosColeccion")
        .variable("id", idColeccion)
        .variable("page", page)
        .variable("filtro", filtros)
        .variable("curados",
            (filtros != null && filtros.getCurados() != null && filtros.getCurados().equalsIgnoreCase("Si")))
        .retrieve("coleccion")
        .toEntity(ColeccionHechosDTO.class).block();
  }

  public void crearColeccion(ColeccionNuevaDTO coleccionDTO) {
    webApiCallerService.post(agregadorServiceUrl + "/colecciones", coleccionDTO, Void.class);
  }

  public Coleccion obtenerColeccionPorId(String idColeccion) {
    ResponseEntity<Coleccion> response = restTemplate.exchange(
        agregadorServiceUrl + "/colecciones/" + idColeccion,
        HttpMethod.GET,
        null,
        Coleccion.class);
    return response.getBody();
  }

  public void actualizarColeccion(String idColeccion, ColeccionNuevaDTO coleccion) {
    webApiCallerService.put(agregadorServiceUrl + "/colecciones/" + idColeccion, coleccion, Void.class);
  }

  public void eliminarColeccion(String idColeccion) {
    webApiCallerService.delete(agregadorServiceUrl + "/colecciones/" + idColeccion);
  }

  public HechoDetallesDTO getDetallesHecho(Long idHecho) {
    return gqlAgregadorClient.documentName("getHechoById")
        .variable("id", idHecho)
        .retrieve("hecho")
        .toEntity(HechoDetallesDTO.class).block();
  }

  public ResumenActividadDTO obtenerResumenActividad() {
    return webApiCallerService.get(agregadorServiceUrl + "/resumen", ResumenActividadDTO.class);
  }

  public void enviarSolicitudEliminacion(SolicitudEliminacionDTO solicitud) {
    restTemplate.exchange(
        agregadorServiceUrl + "/solicitudes",
        HttpMethod.POST,
        new HttpEntity<>(solicitud),
        Void.class);
  }

  public SolicitudEliminacionPaginadaDTO obtenerSolicitudesEliminacion(int page, Boolean pendientes,
      Boolean filterByCreator) {
    String url = UriComponentsBuilder.fromUriString(agregadorServiceUrl)
        .path("/solicitudes")
        .queryParam("page", page)
        .queryParam("pendientes", pendientes)
        .queryParam("filterByCreator", filterByCreator)
        .build()
        .toUriString();

    return webApiCallerService.get(url, SolicitudEliminacionPaginadaDTO.class);
  }

  public SolicitudEliminacionDetallesDTO obtenerSolicitudEliminacion(Long idSolicitud) {
    return gqlAgregadorClient.documentName("getSolicitudEliminacion")
        .variable("id", idSolicitud)
        .retrieve("solicitud")
        .toEntity(SolicitudEliminacionDetallesDTO.class).block();
  }

  // public SolicitudEliminacionDetallesDTO obtenerSolicitud(Long idSolicitud) {
  // return webApiCallerService.get(agregadorServiceUrl + "/solicitudes/" +
  // idSolicitud, SolicitudEliminacionDetallesDTO.class);
  // }

  public void aceptarSolicitudEliminacion(Long idSolicitud) {
    webApiCallerService.put(agregadorServiceUrl + "/solicitudes/" + idSolicitud + "/aceptar", Void.class, Void.class);
  }

  public void rechazarSolicitudEliminacion(Long idSolicitud) {
    webApiCallerService.put(agregadorServiceUrl + "/solicitudes/" + idSolicitud + "/denegar", Void.class, Void.class);
  }

  public List<String> obtenerProvincias() {
    try {
      ResponseEntity<List<String>> response = restTemplate.exchange(
          agregadorServiceUrl + "/ubicaciones/provincias",
          HttpMethod.GET,
          null,
          new ParameterizedTypeReference<List<String>>() {
          });
      return response.getBody();
    } catch (Exception e) {
      log.error("Error al obtener provincias: ", e);
      return new ArrayList<>();
    }
  }

  public List<String> obtenerMunicipios() {
    try {
      return restTemplate.exchange(
          agregadorServiceUrl + "/ubicaciones/municipios",
          HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {
          }).getBody();
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

  public List<String> obtenerDepartamentos() {
    try {
      return restTemplate.exchange(
          agregadorServiceUrl + "/ubicaciones/departamentos",
          HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {
          }).getBody();
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }
}
