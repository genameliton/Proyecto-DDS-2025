package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.CambioAlgoritmoDTO;
import ar.edu.utn.frba.dds.models.dtos.input.ColeccionDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.dtos.output.ColeccionGQLDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDetallesDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.PaginacionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.ResumenActividadDTOSalida;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.EstadoColeccion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.factories.FiltroStrategyFactory;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.*; // Importante para el instanceof
import ar.edu.utn.frba.dds.models.entities.utils.ColeccionConverter;
import ar.edu.utn.frba.dds.models.entities.utils.FuenteConverter;
import ar.edu.utn.frba.dds.models.entities.utils.HechoConverter;
import ar.edu.utn.frba.dds.models.events.FuentesAProcesarEvent;
import ar.edu.utn.frba.dds.models.repositories.IColeccionRepository;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechoRepository;
import ar.edu.utn.frba.dds.models.repositories.IOrigenRepository;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudEliminacionRepository;
import ar.edu.utn.frba.dds.models.repositories.specs.HechoSpecs; // Tu nueva clase de Specs

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional; // Importante usar el de Spring

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class ColeccionService {
  private final IColeccionRepository coleccionRepository;
  private final SolicitudEliminacionService solicitudService;
  private final IHechoRepository hechoRepository;
  private final FuenteConverter fuenteConverter;
  private final ColeccionConverter coleccionConverter;
  private final HechoConverter hechoConverter;
  private final IFuenteRepository fuenteRepository;
  private final ISolicitudEliminacionRepository solicitudRepository;
  private final ApplicationEventPublisher eventPublisher;

  public ColeccionService(
      IColeccionRepository coleccionRepository,
      SolicitudEliminacionService solicitudService,
      IHechoRepository hechoRepository,
      IOrigenRepository origenRepo,
      FuenteConverter fuenteConverter,
      ColeccionConverter coleccionConverter,
      HechoConverter hechoConverter,
      IFuenteRepository fuenteRepository,
      ISolicitudEliminacionRepository solicitudRepository,
      ApplicationEventPublisher eventPublisher) {
    this.coleccionRepository = coleccionRepository;
    this.solicitudService = solicitudService;
    this.hechoRepository = hechoRepository;
    this.fuenteConverter = fuenteConverter;
    this.coleccionConverter = coleccionConverter;
    this.hechoConverter = hechoConverter;
    this.fuenteRepository = fuenteRepository;
    this.solicitudRepository = solicitudRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public ColeccionDTOSalida createColeccion(ColeccionDTOEntrada dto) {
    Coleccion coleccion = new Coleccion();
    coleccion.setTitulo(dto.getTitulo());
    coleccion.setDescripcion(dto.getDescripcion());

    if (dto.getAlgoritmoConsenso() != null && !dto.getAlgoritmoConsenso().isEmpty()) {
      try {
        TipoAlgoritmo tipoAlgoritmo = TipoAlgoritmo.valueOf(dto.getAlgoritmoConsenso());
        coleccion.setAlgoritmoConsenso(tipoAlgoritmo.getStrategy());
        coleccion.refrescarHechosCurados();
      } catch (Exception e) {
        throw new IllegalArgumentException("Algoritmo de tipo " + dto.getAlgoritmoConsenso() + " no aceptado");
      }
    }

    if (dto.getFuentes() != null) {
      Set<Fuente> fuentes = new HashSet<>();
      dto.getFuentes().forEach(fuenteDTO -> {
        Fuente fuente = fuenteConverter.fromDTO(fuenteDTO);
        Optional<Fuente> fuenteExistente = fuenteRepository.findByUrlAndTipoFuente(
            fuente.getUrl(),
            fuente.getTipoFuente());

        if (fuenteExistente.isPresent()) {
          fuente = fuenteExistente.get();
        } else {
          fuente = fuenteRepository.save(fuente);
        }
        fuentes.add(fuente);
      });
      coleccion.setearFuentes(fuentes);
    }

    if (dto.getCriterios() != null) {
      Set<IFiltroStrategy> criterios = dto.getCriterios().stream()
          .map(FiltroStrategyFactory::fromDTO).collect(Collectors.toSet());
      coleccion.setearCriterios(criterios);
    }

    Coleccion coleccionGuardada = coleccionRepository.save(coleccion);

    List<String> idsFuentes = coleccionGuardada.getFuentes().stream()
        .map(Fuente::getId)
        .toList();

    eventPublisher.publishEvent(new FuentesAProcesarEvent(coleccionGuardada.getId(), idsFuentes));

    log.info("EVENTO_CREACIÓN - Colección creada exitosamente. ID: {}, Título: '{}'",
        coleccionGuardada.getId(),
        coleccionGuardada.getTitulo());
    return coleccionConverter.fromEntity(coleccionGuardada);
  }

  @Transactional(readOnly = true)
  public List<ColeccionDTOSalida> getColeccionesDTO() {
    List<Coleccion> colecciones = coleccionRepository.findAll();
    return colecciones.stream().map(coleccionConverter::fromEntity).toList();
  }

  @Transactional(readOnly = true)
  public List<Coleccion> getColecciones() {
    return coleccionRepository.findAll();
  }

  @Transactional(readOnly = true)
  public ColeccionDTOSalida getColeccionDTO(String coleccionId) {
    Coleccion coleccion = this.getColeccion(coleccionId);
    ColeccionDTOSalida respuesta = coleccionConverter.fromEntity(coleccion);
    respuesta.setCantSolicitudesSpam(this.obtenerCantSolicitudesSpam(coleccion.getHechos()));
    return respuesta;
  }

  private Integer obtenerCantSolicitudesSpam(Set<Hecho> hechos) {
    AtomicReference<Integer> cantidadSolicitudesSpam = new AtomicReference<>(0);
    hechos.forEach(h -> {
      cantidadSolicitudesSpam
          .getAndSet(cantidadSolicitudesSpam.get() + solicitudService.cantidadSolicitudesSpam(h.getId()));
    });
    return cantidadSolicitudesSpam.get();
  }

  @Transactional(readOnly = true)
  public Coleccion getColeccion(String coleccionId) {
    return coleccionRepository
        .findById(coleccionId)
        .orElseThrow(() -> new EntityNotFoundException("Coleccion con id " + coleccionId + " no encontrada"));
  }

  @Transactional
  public void updateColeccion(String coleccionId, ColeccionDTOEntrada dto) {
    Coleccion coleccion = this.getColeccion(coleccionId);

    if (dto.getTitulo() != null)
      coleccion.setTitulo(dto.getTitulo());
    if (dto.getDescripcion() != null)
      coleccion.setDescripcion(dto.getDescripcion());

    List<String> idsFuentesParaProcesar = new ArrayList<>();

    if (dto.getFuentes() != null) {
      Set<Fuente> fuentes = new HashSet<>();
      dto.getFuentes().forEach(fuenteDTO -> {
        Fuente fuente = fuenteConverter.fromDTO(fuenteDTO);
        Optional<Fuente> fuenteExistente = fuenteRepository.findByUrlAndTipoFuente(fuente.getUrl(),
            fuente.getTipoFuente());

        if (fuenteExistente.isPresent()) {
          fuente = fuenteExistente.get();
          if (fuente.getTipoFuente() == TipoFuente.DINAMICA) {
            coleccion.setEstado(EstadoColeccion.PROCESANDO);
            idsFuentesParaProcesar.add(fuente.getId());
          }
        } else {
          fuente = fuenteRepository.save(fuente);
          coleccion.setEstado(EstadoColeccion.PROCESANDO);
          idsFuentesParaProcesar.add(fuente.getId());
        }
        fuentes.add(fuente);
      });
      coleccion.setearFuentes(fuentes);
    } else {
      coleccion.clearFuentes();
    }

    if (dto.getAlgoritmoConsenso() != null && !dto.getAlgoritmoConsenso().isEmpty()) {
      try {
        TipoAlgoritmo tipoAlgoritmo = TipoAlgoritmo.valueOf(dto.getAlgoritmoConsenso().toUpperCase());
        IConsensoStrategy algoritmoConsenso = tipoAlgoritmo.getStrategy();
        coleccion.setAlgoritmoConsenso(algoritmoConsenso);
        coleccion.refrescarHechosCurados();
      } catch (Exception e) {
        throw new IllegalArgumentException("Algoritmo de tipo " + dto.getAlgoritmoConsenso() + " no aceptado");
      }
    } else {
      coleccion.setAlgoritmoConsenso(null);
    }

    if (dto.getCriterios() != null) {
      Set<IFiltroStrategy> criterios = dto.getCriterios().stream()
          .map(FiltroStrategyFactory::fromDTO).collect(Collectors.toSet());

      coleccion.setearCriterios(criterios);
    } else {
      coleccion.clearCriterios();
    }

    Coleccion coleccionGuardada = coleccionRepository.save(coleccion);

    if (!idsFuentesParaProcesar.isEmpty()) {
      eventPublisher.publishEvent(new FuentesAProcesarEvent(coleccion.getId(), idsFuentesParaProcesar));
      log.info("EVENTO_UPDATE - Se disparó procesamiento/refresco para las fuentes {}.", idsFuentesParaProcesar);
    }

    log.info("EVENTO_MODIFICACIÓN - Colección actualizada. ID: {}, Titulo: '{}'",
        coleccionGuardada.getId(), coleccionGuardada.getTitulo());
  }

  @Transactional
  public void deleteColeccion(String coleccionId) {
    coleccionRepository.deleteById(coleccionId);
    log.info("EVENTO_ELIMINACION - Colección eliminada. ID: {}", coleccionId);
  }

  @Transactional
  public void procesarColeccionesPendientes() {
    List<Coleccion> coleccionesPendientes = coleccionRepository.findByEstado(EstadoColeccion.PROCESANDO);

    if (coleccionesPendientes.isEmpty())
      return;

    log.info("Se encontraron {} colecciones pendientes.", coleccionesPendientes.size());

    for (Coleccion c : coleccionesPendientes) {
      try {
        c.getFuentes().forEach(fuente -> {
          List<String> idsFuentes = new ArrayList<>();
          idsFuentes.add(fuente.getId());
          eventPublisher.publishEvent(new FuentesAProcesarEvent(c.getId(), idsFuentes));
        });
      } catch (Exception e) {
        log.warn("No se pudo actualizar la colección {}. Se intentará luego. Error: {}", c.getId(),
            e.getMessage());
      }
    }
  }

  @Transactional
  public void refrescoFuentes() {
    List<Fuente> fuentes = fuenteRepository.findAll();
    if (fuentes.isEmpty())
      return;
    List<String> todosLosIds = fuentes.stream()
        .map(Fuente::getId)
        .toList();
    eventPublisher.publishEvent(new FuentesAProcesarEvent(null, todosLosIds));
    log.info("Se disparó el refresco masivo para {} fuentes.", todosLosIds.size());
  }

  @Transactional
  public void refrescarFuenteDinamica() {
    Optional<Fuente> fuenteDinamica = fuenteRepository.findByTipoFuente(TipoFuente.DINAMICA);
    if (fuenteDinamica.isPresent()) {
      String fuenteId = fuenteDinamica.get().getId();
      eventPublisher.publishEvent(new FuentesAProcesarEvent(null, List.of(fuenteId)));
      log.info("Se solicitó refresco manual para fuente dinamica {}", fuenteId);
    }
  }

  @Transactional(readOnly = true)
  public PaginacionDTOSalida<HechoDTOSalida> getHechos(String coleccionId, boolean navegacionCurada, Integer page,
      Set<IFiltroStrategy> filtrosUsuario) {

    int pageSize = 500;
    int pageNumber = (page == null || page < 1) ? 0 : page - 1;
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("fechaAcontecimiento").descending());

    // 2. Especificación Base
    Specification<Hecho> spec = Specification.where(HechoSpecs.excluirEliminados());

    // 3. Lógica de Colección
    if (coleccionId != null) {
      Coleccion coleccion = coleccionRepository.findById(coleccionId)
          .orElseThrow(() -> new EntityNotFoundException("Coleccion no encontrada"));

      if (coleccion.getCriterios() != null && !coleccion.getCriterios().isEmpty()) {
        for (IFiltroStrategy criterioInterno : coleccion.getCriterios()) {
          spec = spec.and(convertirFiltroASpec(criterioInterno));
        }
      }

      if (navegacionCurada) {
        if (coleccion.getAlgoritmoConsenso() != null) {
          spec = spec.and(HechoSpecs.deConsenso(coleccion.getAlgoritmoConsenso().getId()));
        } else {
          return new PaginacionDTOSalida<>(new ArrayList<>(), 1, 0);
        }
      } else {
        List<String> fuenteIds = coleccion.getFuentes().stream().map(Fuente::getId).toList();
        if (fuenteIds.isEmpty()) {
          return new PaginacionDTOSalida<>(new ArrayList<>(), 1, 0);
        }
        spec = spec.and(HechoSpecs.deFuentes(fuenteIds));
      }
    }

    if (filtrosUsuario != null && !filtrosUsuario.isEmpty()) {
      for (IFiltroStrategy filtroExterno : filtrosUsuario) {
        spec = spec.and(convertirFiltroASpec(filtroExterno));
      }
    }

    Page<Hecho> pageResult = hechoRepository.findAll(spec, pageable);

    List<HechoDTOSalida> dtos = pageResult.getContent().stream()
        .map(hechoConverter::fromEntity)
        .collect(Collectors.toList());

    return new PaginacionDTOSalida<>(
        dtos,
        pageResult.getNumber() + 1,
        pageResult.getTotalPages());
  }

  private Specification<Hecho> convertirFiltroASpec(IFiltroStrategy filtro) {
    if (filtro instanceof FiltroCategoria f) {
      return HechoSpecs.conCategoria(f.getNombreCategoria());
    } else if (filtro instanceof FiltroProvincia f) {
      return HechoSpecs.enProvincia(f.getProvincia());
    } else if (filtro instanceof FiltroMunicipio f) {
      return HechoSpecs.enMunicipio(f.getMunicipio());
    } else if (filtro instanceof FiltroDepartamento f) {
      return HechoSpecs.enDepartamento(f.getDepartamento());
    } else if (filtro instanceof FiltroFuente f) {
      if (f.getTipoFuente() != null) {
        return HechoSpecs.conTipoFuente(f.getTipoFuente().toString());
      }
    } else if (filtro instanceof FiltroFechaAcontecimiento f) {
      LocalDate inicio = f.getFechaInicio() != null ? f.getFechaInicio().toLocalDate() : null;
      LocalDate fin = f.getFechaFinal() != null ? f.getFechaFinal().toLocalDate() : null;
      return HechoSpecs.fechaAcontecimientoEntre(inicio, fin);
    } else if (filtro instanceof FiltroFechaReporte f) {
      LocalDate inicio = f.getFechaInicio() != null ? f.getFechaInicio().toLocalDate() : null;
      LocalDate fin = f.getFechaFinal() != null ? f.getFechaFinal().toLocalDate() : null;
      return HechoSpecs.fechaReporteEntre(inicio, fin);
    }
    return Specification.where(null);
  }

  @Transactional
  public void addFuente(String coleccionId, FuenteDTO dto) {
    Fuente fuente = fuenteConverter.fromDTO(dto);
    Coleccion coleccion = this.getColeccion(coleccionId);
    coleccion.addFuente(fuente);
    coleccionRepository.save(coleccion);
  }

  @Transactional
  public void removeFuente(String coleccionId, String fuenteId) {
    Coleccion coleccion = this.getColeccion(coleccionId);
    coleccion.removeFuente(fuenteId);
    coleccionRepository.save(coleccion);
  }

  @Transactional
  public void updateAlgoritmoConsenso(String coleccionId, CambioAlgoritmoDTO algoritmoDTO) {
    ColeccionDTOEntrada dto = new ColeccionDTOEntrada();
    dto.setAlgoritmoConsenso(algoritmoDTO.getTipoAlgoritmo());
    updateColeccion(coleccionId, dto);
  }

  @Transactional
  public void refrescarHechosCurados() {
    List<Coleccion> colecciones = coleccionRepository.findAll();
    colecciones.forEach(Coleccion::refrescarHechosCurados);
    coleccionRepository.saveAll(colecciones);
  }

  @Transactional
  public void addCriterio(String id, IFiltroStrategy filtro) {
    Coleccion coleccion = this.getColeccion(id);
    coleccion.addCriterio(filtro);
    coleccionRepository.save(coleccion);
  }

  @Transactional(readOnly = true)
  public HechoDetallesDTOSalida getHechoDTO(Long idHecho) {
    Hecho hecho = this.getHechoById(idHecho);
    return hechoConverter.fromEntityDetails(hecho);
  }

  @Transactional(readOnly = true)
  Hecho getHechoById(Long idHecho) {
    return hechoRepository.findById(idHecho)
        .orElseThrow(() -> new EntityNotFoundException("Hecho con id " + idHecho + " no encontrada"));
  }

  @Transactional(readOnly = true)
  public ResumenActividadDTOSalida getResumenActividad() {
    ResumenActividadDTOSalida resumenActividadDTO = new ResumenActividadDTOSalida();
    resumenActividadDTO.setHechostotales(hechoRepository.count());
    resumenActividadDTO.setFuentesTotales(fuenteRepository.count());
    resumenActividadDTO.setSolicitudesEliminacion(solicitudRepository.countByEstadoActual_Estado(TipoEstado.PENDIENTE));
    return resumenActividadDTO;
  }

  @Transactional(readOnly = true)
  public ColeccionGQLDTOSalida getColeccionOutputDTO(String id, Boolean curadosFinal, Integer page,
      Set<IFiltroStrategy> filtros) {
    Coleccion coleccion = this.getColeccion(id);
    ColeccionDTOSalida coleccionDTO = coleccionConverter.fromEntity(coleccion);
    ColeccionGQLDTOSalida respuesta = new ColeccionGQLDTOSalida(coleccionDTO);
    respuesta.setHechos(this.getHechos(id, curadosFinal, page, filtros));
    return respuesta;
  }
}