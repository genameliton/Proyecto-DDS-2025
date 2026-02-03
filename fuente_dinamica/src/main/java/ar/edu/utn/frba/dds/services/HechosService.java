package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.exceptions.RecursoNoEncontradoException;
import ar.edu.utn.frba.dds.mappers.HechoMapper;
import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.HechoUpdateDTO;
import ar.edu.utn.frba.dds.models.dtos.input.RevisionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoRevisionOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.MultimediaOutputDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Multimedia;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.enums.EstadoHecho;
import ar.edu.utn.frba.dds.models.repositories.IHechosRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class HechosService {
  private final IHechosRepository hechosRepository;
  private final MultimediaService multimediaService;
  private final AgregadorService agregadorService;

  @Value("${modification.allowance-days}")
  private long DIAS_EDICION_PERMITIDOS;

  public HechosService(
      IHechosRepository hechosRepository,
      MultimediaService multimediaService,
      AgregadorService agregadorService) {
    this.hechosRepository = hechosRepository;
    this.multimediaService = multimediaService;
    this.agregadorService = agregadorService;
  }

  private boolean sePuedeEditarHecho(Hecho hecho) {
    var fechaLimite = hecho.getFechaCarga().plusDays(DIAS_EDICION_PERMITIDOS);
    return hecho.getFechaCarga().isBefore(fechaLimite);
  }

  public List<HechoOutputDTO> getHechos() {
    List<Hecho> hechos = hechosRepository.findHechosAceptados();

    return hechos.stream()
        .map(HechoMapper::toHechoOutputDTO)
        .collect(Collectors.toList());
  }

  public HechoOutputDTO getHechoById(Long id) {
    Hecho hecho = hechosRepository
        .findById(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("Hecho no encontrado con id: " + id));

    return HechoMapper.toHechoOutputDTO(hecho);
  }

  public HechoOutputDTO crearHecho(HechoInputDTO hechoDto, List<MultipartFile> multimedia) {
    Hecho hecho = Hecho.builder()
        .titulo(hechoDto.getTitulo())
        .descripcion(hechoDto.getDescripcion())
        .categoria(hechoDto.getCategoria())
        .ubicacion(new Ubicacion(hechoDto.getLatitud(), hechoDto.getLongitud()))
        .fechaAcontecimiento(hechoDto.getFechaAcontecimiento())
        .nombreAutor(hechoDto.getAutor())
        .build();

    if (multimedia != null && !multimedia.isEmpty()) {
      List<MultipartFile> validFiles = multimedia.stream()
          .filter(f -> f != null && !f.isEmpty())
          .toList();

      if (!validFiles.isEmpty()) {
        validFiles.forEach(file -> {
          try {
            Multimedia multimediaEntity = multimediaService.guardarArchivo(file);
            hecho.addMultimedia(multimediaEntity);
          } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("Error al procesar archivo multimedia: " + e.getMessage(), e);
          }
        });
      }
    }
    Hecho hechoGuardado = hechosRepository.save(hecho);

    List<MultimediaOutputDTO> multimediaDto = hechoGuardado.getMultimedia().stream()
        .map(m -> MultimediaOutputDTO.builder()
            .nombre(m.getNombre())
            .ruta(m.getRuta())
            .formato(m.getFormato().name().toLowerCase())
            .build())
        .toList();
    log.info("Nuevo hecho guardado: Titulo: {}, Categoria: {}", hechoGuardado.getTitulo(),
        hechoGuardado.getCategoria());

    return HechoOutputDTO.builder()
        .id(hechoGuardado.getId())
        .titulo(hechoGuardado.getTitulo())
        .descripcion(hechoGuardado.getDescripcion())
        .categoria(hechoGuardado.getCategoria())
        .latitud(hechoGuardado.getUbicacion().getLatitud())
        .longitud(hechoGuardado.getUbicacion().getLongitud())
        .fechaHecho(hechoGuardado.getFechaAcontecimiento())
        .createdAt(hechoGuardado.getFechaCarga())
        .updatedAt(hechoGuardado.getFechaUltimaModificacion())
        .multimedia(multimediaDto)
        .autor(hechoGuardado.getNombreAutor())
        .build();
  }

  public HechoOutputDTO actualizarHecho(Long id, HechoUpdateDTO hechoDto, List<MultipartFile> multimedia,
      String username) {

    Hecho hecho = hechosRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Hecho no encontrado"));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMINISTRADOR"));

    if (!isAdmin && !username.equals(hecho.getNombreAutor())) {
      throw new IllegalStateException("No tienes permiso para editar este hecho");
    }

    if (!sePuedeEditarHecho(hecho)) {
      throw new IllegalStateException(String.format(
          "El período de edición ha expirado. Solo puedes editar los hechos dentro de los %d dias posteriores a su edición",
          DIAS_EDICION_PERMITIDOS));
    }

    if (hechoDto.getTitulo() != null) {
      hecho.setTitulo(hechoDto.getTitulo());
    }
    if (hechoDto.getDescripcion() != null) {
      hecho.setDescripcion(hechoDto.getDescripcion());
    }
    if (hechoDto.getCategoria() != null) {
      hecho.setCategoria(hechoDto.getCategoria());
    }
    if (hechoDto.getLatitud() != null) {
      hecho.getUbicacion().setLatitud(hechoDto.getLatitud());
    }
    if (hechoDto.getLongitud() != null) {
      hecho.getUbicacion().setLongitud(hechoDto.getLongitud());
    }
    hecho.setFechaUltimaModificacion(LocalDateTime.now());

    if (hecho.getEstadoHecho() == EstadoHecho.ACEPTADO ||
        hecho.getEstadoHecho() == EstadoHecho.ACEPTADO_CON_SUGERENCIAS) {

      log.info("Hecho ID {} modificado tras aprobación. Revirtiendo estado a PENDIENTE para nueva revisión.",
          hecho.getId());

      hecho.setEstadoHecho(EstadoHecho.PENDIENTE);
      hecho.setRevisadoPor(null);
      hecho.setMotivoRechazo(null);
      hecho.setSugerencias(null);
    }
    hecho.getMultimedia().clear();

    if (multimedia != null && !multimedia.isEmpty()) {
      List<MultipartFile> validFiles = multimedia.stream()
          .filter(f -> f != null && !f.isEmpty())
          .toList();

      if (!validFiles.isEmpty()) {
        validFiles.forEach(file -> {
          try {
            Multimedia multimediaEntity = multimediaService.guardarArchivo(file);
            hecho.addMultimedia(multimediaEntity);
          } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("Error al procesar archivo multimedia: " + e.getMessage(), e);
          }
        });
      }
    }

    Hecho hechoActualizado = hechosRepository.save(hecho);
    return HechoMapper.toHechoOutputDTO(hechoActualizado);
  }

  public List<HechoRevisionOutputDTO> getHechosPendientes() {
    List<Hecho> hechosPendientes = hechosRepository.findHechosPendientes();

    return hechosPendientes.stream()
        .map(this::toHechoRevisionOutputDTO)
        .collect(Collectors.toList());
  }

  public HechoRevisionOutputDTO aceptarHecho(Long id, RevisionInputDTO revisionDto) {
    Hecho hecho = hechosRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Hecho no encontrado con id: " + id));

    if (!hecho.estaPendiente()) {
      throw new RuntimeException("El hecho ya fue revisado anteriormente");
    }

    hecho.aceptar(revisionDto.getSupervisor());
    Hecho hechoActualizado = hechosRepository.save(hecho);

    log.info("Hecho aceptado: Titulo: {}, Categoria: {}", hechoActualizado.getTitulo(),
        hechoActualizado.getCategoria());

    agregadorService.notificarCambios();

    return toHechoRevisionOutputDTO(hechoActualizado);
  }

  public HechoRevisionOutputDTO aceptarHechoConSugerencias(Long id, RevisionInputDTO revisionDto) {
    Hecho hecho = hechosRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Hecho no encontrado con id: " + id));

    if (!hecho.estaPendiente()) {
      throw new RuntimeException("El hecho ya fue revisado anteriormente");
    }
    hecho.aceptarConSugerencias(revisionDto.getSupervisor(), revisionDto.getComentario());
    Hecho hechoActualizado = hechosRepository.save(hecho);

    log.info("Hecho aceptado: Titulo: {}, Categoria: {}", hechoActualizado.getTitulo(),
        hechoActualizado.getCategoria());

    agregadorService.notificarCambios();

    return toHechoRevisionOutputDTO(hechoActualizado);
  }

  public HechoRevisionOutputDTO rechazarHecho(Long id, RevisionInputDTO revisionDto) {
    Hecho hecho = hechosRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Hecho no encontrado con id: " + id));

    hecho.rechazar(revisionDto.getSupervisor(), revisionDto.getComentario());
    Hecho hechoActualizado = hechosRepository.save(hecho);

    log.info("Hecho rechazado: Titulo: {}, Categoria: {}", hechoActualizado.getTitulo(),
        hechoActualizado.getCategoria());

    agregadorService.notificarCambios();

    return toHechoRevisionOutputDTO(hechoActualizado);
  }

  public List<HechoRevisionOutputDTO> getHechosPendientesByCreador() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username;

    if (authentication.getPrincipal() instanceof UserDetails userDetails) {
      username = userDetails.getUsername();
    } else {
      username = authentication.getPrincipal().toString();
    }

    List<Hecho> hechosUsuario = hechosRepository.findHechosByCreator(username);

    return hechosUsuario.stream()
        .map(this::toHechoRevisionOutputDTO)
        .collect(Collectors.toList());
  }

  private HechoRevisionOutputDTO toHechoRevisionOutputDTO(Hecho hecho) {
    return HechoRevisionOutputDTO.builder()
        .id(hecho.getId())
        .titulo(hecho.getTitulo())
        .descripcion(hecho.getDescripcion())
        .categoria(hecho.getCategoria())
        .latitud(hecho.getUbicacion().getLatitud())
        .longitud((hecho.getUbicacion().getLongitud()))
        .fecha_acontecimiento(hecho.getFechaAcontecimiento())
        .fecha_carga(hecho.getFechaCarga())
        .estado_hecho(hecho.getEstadoHecho())
        .motivo_rechazo(hecho.getMotivoRechazo())
        .sugerencias(hecho.getSugerencias())
        .fecha_revision(LocalDateTime.now())
        .revisado_por(hecho.getRevisadoPor())
        .build();
  }
}