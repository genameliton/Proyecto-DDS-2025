package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.externalApi.SpamApi;
import ar.edu.utn.frba.dds.models.dtos.input.RevisionHechoDTO;
import ar.edu.utn.frba.dds.models.dtos.input.SolicitudDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.output.PaginacionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudEliminacionDTOSalida;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.utils.SolicitudConverter;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudEliminacionRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class SolicitudEliminacionService {
  private final ISolicitudEliminacionRepository solicitudesEliminacionRepo;
  private final SpamApi detectorSpam;
  private final SolicitudConverter solicitudConverter;
  private final IFuenteRepository fuenteRepository;
  private final WebClient webClient;

  public SolicitudEliminacionService(
      ISolicitudEliminacionRepository solicitudesEliminacionRepo,
      SpamApi detectorSpam,
      SolicitudConverter solicitudConverter,
      IFuenteRepository fuenteRepository,
      WebClient.Builder webClientBuilder) {
    this.solicitudesEliminacionRepo = solicitudesEliminacionRepo;
    this.detectorSpam = detectorSpam;
    this.solicitudConverter = solicitudConverter;
    this.fuenteRepository = fuenteRepository;
    this.webClient = webClientBuilder.build();
  }

  @Transactional
  public void createSolicitudEliminacion(SolicitudDTOEntrada dtoSolicitud) {
    Solicitud solicitud = solicitudConverter.fromDTO(dtoSolicitud);
    Solicitud solicitudGuardada = solicitudesEliminacionRepo.save(solicitud);
    log.info("Nueva solicitud de eliminacion, Titulo: {}", solicitudGuardada.getTitulo());
    if (detectorSpam.esSpam(solicitud.getTexto()) || !solicitud.estaFundado()) {
      this.marcarComoSpam(solicitudGuardada.getId());
      this.rechazarSolicitudEliminacion(solicitudGuardada.getId());
    }
  }

  private void marcarComoSpam(Long id) {
    Solicitud solicitud = this.getSolicitudEliminacionById(id);
    solicitud.marcarSpam();
    log.info("Solicitud marcada como spam");
    solicitudesEliminacionRepo.save(solicitud);
  }

  public void rechazarSolicitudEliminacion(Long id) {
    Solicitud solicitud = this.getSolicitudEliminacionById(id);
    solicitud.rechazar();
    log.info("Solicitud rechazada, Id: {}, Titulo: {}", solicitud.getId(), solicitud.getTitulo());
    solicitudesEliminacionRepo.save(solicitud);
  }

  @Transactional
  public void aceptarSolicitudEliminacion(Long id) {
    Solicitud solicitud = this.getSolicitudEliminacionById(id);
    solicitud.aceptar();
    log.info("Solicitud aceptada, Id: {}, Titulo: {}", solicitud.getId(), solicitud.getTitulo());
    try {
      Hecho hecho = solicitud.getHecho();
      List<Fuente> fuentesDelHecho = fuenteRepository.findFuentesByHechoId(hecho.getId());

      for (Fuente fuente : fuentesDelHecho) {
        if (fuente.getTipoFuente() == TipoFuente.DINAMICA) {
          Long idEnFuente = hecho.getIdExterno();
          if (idEnFuente != null) {
            String urlDestino = fuente.getUrl() + "/hechos/" + idEnFuente + "/rechazar";

            RevisionHechoDTO revision = new RevisionHechoDTO();
            revision.setSupervisor("Sistema de Eliminación Automática");
            revision.setComentario("Rechazado por solicitud de eliminación. Motivo: " + solicitud.getTexto());
            
            log.info("PROPAGANDO ELIMINACIÓN: Llamando a {}", urlDestino);
            webClient.put()
                .uri(urlDestino)
                .header("Authorization", "Bearer " + obtenerTokenActual())
                .bodyValue(revision)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                    succ -> log.info("Fuente notificada correctamente."),
                    err -> log.error("Error al notificar fuente: {}", err.getMessage()));
          }

        }
      }
    } catch (Exception e) {
      log.error("Error intentando propagar la eliminación a la fuente original", e);
    }
    solicitudesEliminacionRepo.save(solicitud);
  }

  public Boolean hechoEliminado(Hecho hecho) {
    List<Solicitud> solicitudes = solicitudesEliminacionRepo.findAll();
    return solicitudes.stream().anyMatch(solicitud -> solicitud.getHecho().getId() == hecho.getId() &&
        solicitud.estaAceptada());
  }

  public PaginacionDTOSalida<SolicitudEliminacionDTOSalida> getSolicitudesEliminacionDTO(
      Integer page,
      Boolean pendientes,
      Boolean filterByCreator) {
    if (filterByCreator == null)
      filterByCreator = false;
    if (pendientes == null)
      pendientes = true;
    int pageNumber = (page == null || page < 1) ? 0 : page - 1;
    int pageSize = 20;
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("fecha").descending());

    Page<Solicitud> pageResult;

    // Si se debe filtrar por creador
    if (filterByCreator) {
      // Obtener usuario autenticado
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String username;

      if (authentication.getPrincipal() instanceof UserDetails userDetails) {
        username = userDetails.getUsername();
      } else {
        username = authentication.getPrincipal().toString();
      }

      // Filtrado según creador y estado
      if (pendientes) {
        pageResult = solicitudesEliminacionRepo.findByCreadorAndEstadoActual(username, TipoEstado.PENDIENTE, pageable);
      } else {
        pageResult = solicitudesEliminacionRepo.findByCreador(username, pageable);
      }

    } else {
      // Sin filtro por creador
      if (pendientes) {
        pageResult = solicitudesEliminacionRepo.findByEstadoActual(TipoEstado.PENDIENTE, pageable);
      } else {
        pageResult = solicitudesEliminacionRepo.findAll(pageable);
      }
    }

    List<SolicitudEliminacionDTOSalida> solicitudesDTO = pageResult.getContent()
        .stream()
        .map(solicitudConverter::fromEntity)
        .toList();

    return new PaginacionDTOSalida<>(
        solicitudesDTO,
        pageable.getPageNumber() + 1,
        pageResult.getTotalPages());
  }

  public Integer cantidadSolicitudesSpam(Long idHecho) {
    AtomicReference<Integer> cantSolicitudesSpam = new AtomicReference<>(0);
    List<Solicitud> solicitudes = solicitudesEliminacionRepo.findAll().stream().filter(s -> s.esSpam()).toList();
    solicitudes.forEach(s -> {
      if (s.getHecho().getId() == idHecho) {
        cantSolicitudesSpam.getAndSet(cantSolicitudesSpam.get() + 1);
      }
    });
    return cantSolicitudesSpam.get();
  }

  public Solicitud getSolicitudEliminacionById(Long id) {
    return solicitudesEliminacionRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));
  }

  public SolicitudDTOSalida getSolicitudEliminacionDTO(Long id) {
    Solicitud solicitud = getSolicitudEliminacionById(id);
    return solicitudConverter.fromEntityDetails(solicitud);
  }

  private String obtenerTokenActual() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
        throw new RuntimeException("No hay autenticación presente en el SecurityContext");
    }

    if (authentication.getPrincipal() instanceof Jwt) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getTokenValue();
    }

    if (authentication.getCredentials() instanceof String) {
        return (String) authentication.getCredentials();
    }
    
    try {
        var attr = (org.springframework.web.context.request.ServletRequestAttributes) 
            org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (attr != null) {
            String bearer = attr.getRequest().getHeader("Authorization");
            if (bearer != null && bearer.startsWith("Bearer ")) {
                return bearer.substring(7);
            }
        }
    } catch (Exception e) {
    }

    throw new RuntimeException("No se encontró token. Principal es tipo: " 
        + authentication.getPrincipal().getClass().getName() 
        + " | Credentials es: " + authentication.getCredentials());
}
}