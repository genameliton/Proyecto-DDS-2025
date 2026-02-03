package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class FuenteDinamicaService {
  private final WebApiCallerService webApiCallerService;
  private final RestTemplate restTemplate;
  private final long diasPermitidosEdicion;
  private final String fuenteDinamicaServiceUrl;

  public FuenteDinamicaService(
      WebApiCallerService webApiCallerService,
      RestTemplate restTemplate,
      @Value("${modification.allowance-days}") long diasPermitidosEdicion,
      @Value("${fuenteDinamica.service.url}") String fuenteDinamicaServiceUrl) {
    this.webApiCallerService = webApiCallerService;
    this.restTemplate = restTemplate;
    this.diasPermitidosEdicion = diasPermitidosEdicion;
    this.fuenteDinamicaServiceUrl = fuenteDinamicaServiceUrl;
  }

  public HechoUpdateDTO obtenerHechoEdicion(Long id) {
    Class<HechoUpdateDTO> responseType = HechoUpdateDTO.class;
    String url = fuenteDinamicaServiceUrl + "/hechos/" + id;

    try {
      var response = restTemplate.getForEntity(url, responseType);

      if (response.getStatusCode().is2xxSuccessful()) {
        HechoUpdateDTO hecho = response.getBody();
        if (hecho != null && hecho.getCreatedAt() != null) {
          long diasDiferencia = ChronoUnit.DAYS.between(hecho.getCreatedAt(), LocalDateTime.now());

          if (diasDiferencia > diasPermitidosEdicion) {
            throw new IllegalArgumentException(
                "El periodo de edición de " + diasPermitidosEdicion + " días ha expirado.");
          }
        }

        return hecho;
      } else {
        throw new RuntimeException("API devolvió código " + response.getStatusCode());
      }
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (HttpClientErrorException e) {
      throw new RuntimeException("Error cliente HTTP: " + e.getStatusCode(), e);
    } catch (Exception e) {
      throw new RuntimeException("Error al procesar solicitud API", e);
    }
  }

  public void crearHecho(HechoManualDTO hechoDTO, List<MultipartFile> multimedia) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<HechoManualDTO> jsonPart = new HttpEntity<>(hechoDTO, jsonHeaders);
    body.add("hecho", jsonPart);

    if (multimedia != null && !multimedia.isEmpty()) {
      for (MultipartFile file : multimedia) {
        if (file.isEmpty())
          continue;

        try {
          ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
              return file.getOriginalFilename();
            }
          };
          body.add("multimedia", fileResource);
        } catch (Exception e) {
          throw new RuntimeException("Error al procesar archivo para la API", e);
        }
      }
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    try {
      restTemplate.postForEntity(
          fuenteDinamicaServiceUrl + "/hechos",
          requestEntity,
          Void.class);

    } catch (Exception e) {
      throw new RuntimeException("Error al comunicarse con la API de Hechos: " + e.getMessage(), e);
    }
  }

  public void editarHecho(Long id, HechoUpdateDTO hechoDTO, List<MultipartFile> multimedia) {
    if (hechoDTO.getTitulo() == null || hechoDTO.getTitulo().isBlank()) {
      throw new IllegalArgumentException("DEBUG: El título del DTO es nulo o vacío después del post.");
    }
    if (hechoDTO.getLatitud() == null || hechoDTO.getLongitud() == null) {
      throw new IllegalArgumentException("DEBUG: Latitud o Longitud son nulas. Revise el JS y los IDs del HTML.");
    }

    String targetUrl = fuenteDinamicaServiceUrl + "/hechos/" + id;

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<HechoUpdateDTO> jsonPart = new HttpEntity<>(hechoDTO, jsonHeaders);
    body.add("hecho", jsonPart);

    if (multimedia != null && !multimedia.isEmpty()) {
      for (MultipartFile file : multimedia) {
        if (file.isEmpty())
          continue;

        try {
          ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
              return file.getOriginalFilename();
            }
          };
          body.add("multimedia", fileResource);
        } catch (Exception e) {
          throw new RuntimeException("Error al procesar archivo para la API", e);
        }
      }
    }

    try {
      webApiCallerService.put(
          targetUrl,
          body,
          Void.class);
    } catch (Exception e) {
      throw new RuntimeException("ERROR durante PUT Multipart a la API de Hechos: " + e.getMessage(), e);
    }
  }

  public List<SolicitudHechoDTO> obtenerSolicitudesHecho() {
    return this.webApiCallerService.getList(fuenteDinamicaServiceUrl + "/hechos/pendientes", SolicitudHechoDTO.class);
  }

  public SolicitudHechoInputDTO obtenerSolicitudById(Long idHecho) {
    return this.webApiCallerService.get(fuenteDinamicaServiceUrl + "/hechos/" + idHecho, SolicitudHechoInputDTO.class);
  }

  public void aceptarSolicitud(Long idHecho, RevisionHechoDTO revisionHechoDTO) {
    this.webApiCallerService.put(fuenteDinamicaServiceUrl + "/hechos/" + idHecho + "/aceptar", revisionHechoDTO,
        Void.class);
  }

  public void rechazarSolicitud(Long idHecho, RevisionHechoDTO revisionHechoDTO) {
    this.webApiCallerService.put(fuenteDinamicaServiceUrl + "/hechos/" + idHecho + "/rechazar", revisionHechoDTO,
        Void.class);
  }

  public void aceptarConSugerencias(Long idHecho, RevisionHechoDTO revisionHechoDTO) {
    this.webApiCallerService.put(fuenteDinamicaServiceUrl + "/hechos/" + idHecho + "/aceptar-con-sugerencias",
        revisionHechoDTO, Void.class);
  }

  public List<SolicitudHechoDTO> obtenerHechosPorCreador() {
    return this.webApiCallerService.getList(fuenteDinamicaServiceUrl + "/hechos/pendientes_por_creador",
        SolicitudHechoDTO.class);
  }
}
