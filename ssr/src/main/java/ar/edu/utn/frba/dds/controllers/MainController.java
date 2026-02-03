package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.*;
import ar.edu.utn.frba.dds.services.AgregadorService;
import ar.edu.utn.frba.dds.services.EstadisticaService;
import ar.edu.utn.frba.dds.services.FuenteDinamicaService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
  private final AgregadorService agregadorService;
  private final EstadisticaService estadisticaService;
  private final FuenteDinamicaService fuenteDinamicaService;
  @Value("${agregador.service.url}")
  private String agregadorUrl;

  @Value("${fuenteDinamica.service.url}")
  private String fuenteDinamicaUrl;

  @Value("${fuenteEstatica.service.url}")
  private String fuenteEstaticaUrl;

  @Value("${fuenteProxyApi.service.url}")
  private String fuenteProxiAPIUrl;

  public MainController(AgregadorService agregadorService, EstadisticaService estadisticaService,
      FuenteDinamicaService fuenteDinamicaService) {
    this.agregadorService = agregadorService;
    this.estadisticaService = estadisticaService;
    this.fuenteDinamicaService = fuenteDinamicaService;
  }

  @GetMapping({ "/", "/home" })
  public String home(Model model) {
    try {
      List<Coleccion> colecciones = agregadorService.obtenerColecciones();

      if (colecciones != null && !colecciones.isEmpty()) {
        String idColeccion = colecciones.get(0).getId();
        ColeccionHechosDTO resultado = agregadorService.getHechosColeccion(
            idColeccion,
            new FiltrosDTO(),
            1);

        if (resultado != null && resultado.getHechos() != null) {
          List<HechoPaginacionDTO> listaHechos = resultado.getHechos().getData();
          int limite = Math.min(listaHechos.size(), 6);

          model.addAttribute("hechos", listaHechos.subList(0, limite));
          model.addAttribute("idColeccion", idColeccion);
        }
      }
    } catch (Exception e) {
      System.err.println("Warning: " + e.getMessage());
    }
    return "home";
  }

  @GetMapping("/hechos-usuario")
  public String visualizarHechosCreadorPor(Model model, HttpServletRequest request) {
    List<SolicitudHechoDTO> hechos = fuenteDinamicaService.obtenerHechosPorCreador();
    model.addAttribute("solicitudesHechos", hechos);
    return "subirHechos/hechosUsuario";
  }

  @GetMapping("/crear-hecho")
  public String mostrarFormularioCrear(Model model) {
    model.addAttribute("hechoDTO", new HechoManualDTO());
    model.addAttribute("esEdicion", false);
    model.addAttribute("tituloPagina", "Reportar un Nuevo Hecho");
    return "subirHechos/formularioHecho";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/revisionHechos")
  public String mostrarRevisionHechos(Model model) {
    List<SolicitudHechoDTO> solicitudesHecho = fuenteDinamicaService.obtenerSolicitudesHecho();
    model.addAttribute("solicitudesHechos", solicitudesHecho);
    return "/revisionHechos";
  }

  @GetMapping("/panel-control/revisionHechos/{idHecho}")
  public String mostrarDetallesSolicitudHecho(@PathVariable Long idHecho, Model model) {
    SolicitudHechoInputDTO solicitud = fuenteDinamicaService.obtenerSolicitudById(idHecho);

    HechoDetallesDTO hechoDTO = new HechoDetallesDTO();
    hechoDTO.setId(idHecho);
    hechoDTO.setTitulo(solicitud.getTitulo());
    hechoDTO.setDescripcion(solicitud.getDescripcion());
    hechoDTO.setCategoria(solicitud.getCategoria());
    hechoDTO.setLatitud(solicitud.getLatitud());
    hechoDTO.setLongitud(solicitud.getLongitud());
    hechoDTO.setNombreAutor(solicitud.getAutor());

    if (solicitud.getFechaHecho() != null) {
      hechoDTO.setFechaAcontecimiento(solicitud.getFechaHecho().toString());
    }

    if (solicitud.getMultimedia() != null) {
      List<MultimediaDTO> mediaList = new ArrayList<>();
      solicitud.getMultimedia().forEach(m -> {
        MultimediaDTO md = new MultimediaDTO();
        md.setNombre(m.getNombre());
        md.setRuta(m.getRuta());
        md.setFormato(m.getFormato());
        mediaList.add(md);
      });
      hechoDTO.setMultimedia(mediaList);
    }
    model.addAttribute("hecho", hechoDTO);
    model.addAttribute("idColeccion", "revision-carga"); // Dummy ID
    model.addAttribute("modoValidacion", true);
    model.addAttribute("hechoId", idHecho); // Necesario para los links de los botones
    return "coleccion/detallesHecho";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/revisionHechos/{idHecho}/aceptar")
  public String aceptarSolicitudHecho(@PathVariable Long idHecho, Model model, HttpServletRequest request) {
    RevisionHechoDTO revisionHechoDTO = new RevisionHechoDTO();
    revisionHechoDTO.setSupervisor(request.getSession().getAttribute("username").toString());
    revisionHechoDTO.setComentario("");

    fuenteDinamicaService.aceptarSolicitud(idHecho, revisionHechoDTO);
    return "redirect:/panel-control/revisionHechos";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/panel-control/revisionHechos/{idHecho}/aceptarConSugerencia")
  public String aceptarSolicitudHecho(@PathVariable Long idHecho, @ModelAttribute RevisionHechoDTO revisionHechoDTO) {
    fuenteDinamicaService.aceptarConSugerencias(idHecho, revisionHechoDTO);
    return "redirect:/panel-control/revisionHechos";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/revisionHechos/{idHecho}/rechazoConSugerencias")
  public String mostrarFormularioSugerenciasRechazo(@PathVariable Long idHecho, Model model,
      HttpServletRequest request) {
    RevisionHechoDTO revisionHechoDTO = new RevisionHechoDTO();
    revisionHechoDTO.setSupervisor(request.getSession().getAttribute("username").toString());
    model.addAttribute("hechoId", idHecho);
    model.addAttribute("revisionHechoDTO", revisionHechoDTO);
    model.addAttribute("accionHecho", "rechazar");
    return "/subirComentariosSolicitud";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/revisionHechos/{idHecho}/aceptarConSugerencias")
  public String mostrarFormularioSugerenciasAceptacion(@PathVariable Long idHecho, Model model,
      HttpServletRequest request) {
    RevisionHechoDTO revisionHechoDTO = new RevisionHechoDTO();
    revisionHechoDTO.setSupervisor(request.getSession().getAttribute("username").toString());
    model.addAttribute("hechoId", idHecho);
    model.addAttribute("revisionHechoDTO", revisionHechoDTO);
    model.addAttribute("accionHecho", "aceptarConSugerencia");
    return "/subirComentariosSolicitud";
  }

  @PostMapping("/panel-control/revisionHechos/{idHecho}/rechazar")
  public String rechazarSolicitudHecho(@PathVariable Long idHecho, @ModelAttribute RevisionHechoDTO revisionHechoDTO) {
    fuenteDinamicaService.rechazarSolicitud(idHecho, revisionHechoDTO);
    return "redirect:/panel-control/revisionHechos";
  }

  @PostMapping("/subir-hecho")
  public String procesarCreacionDeHecho(
      @ModelAttribute HechoManualDTO hechoDTO,
      @RequestParam(value = "multimedia", required = false) List<MultipartFile> multimedia,
      HttpServletRequest request) {
    // hechosService.crearHecho(hechoDTO, multimedia);
    Object username = request.getSession().getAttribute("username");

    if (username != null)
      hechoDTO.setAutor(username.toString());

    fuenteDinamicaService.crearHecho(hechoDTO, multimedia);
    return "redirect:/";
  }

  @GetMapping("/editar-hecho/{id}")
  public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
    HechoUpdateDTO hechoExistente = fuenteDinamicaService.obtenerHechoEdicion(id);
    HechoManualDTO hechoForm = new HechoManualDTO();
    hechoForm.setTitulo(hechoExistente.getTitulo());
    hechoForm.setDescripcion(hechoExistente.getDescripcion());
    hechoForm.setCategoria(hechoExistente.getCategoria());
    hechoForm.setLatitud(hechoExistente.getLatitud());
    hechoForm.setLongitud(hechoExistente.getLongitud());
    hechoForm.setFechaAcontecimiento(hechoExistente.getFechaHecho());
    model.addAttribute("hechoDTO", hechoForm);
    model.addAttribute("esEdicion", true);
    model.addAttribute("idHecho", id);
    model.addAttribute("tituloPagina", "Editar Hecho Existente");
    return "subirHechos/formularioHecho";
  }

  @PostMapping("/editar-hecho/{id}")
  public String procesarEdicionDeHecho(
      @PathVariable Long id,
      @ModelAttribute("hecho") HechoUpdateDTO hechoDTO,
      @RequestParam(value = "multimedia", required = false) List<MultipartFile> multimediaFiles,
      RedirectAttributes redirectAttributes) {
    try {
      fuenteDinamicaService.editarHecho(id, hechoDTO, multimediaFiles);
      return "redirect:/hechos-usuario";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error al intentar editar el hecho: " + e.getMessage());
      return "redirect:/editar-hecho/" + id;
    }
  }

  @PostMapping("/solicitarEliminacion")
  public String procesarSolicitudEliminacion(@ModelAttribute("solicitudEliminacion") SolicitudEliminacionDTO solicitud,
      HttpServletRequest request) {
    Object username = request.getSession().getAttribute("username");

    if (username != null)
      solicitud.setCreador(username.toString());
    else
      solicitud.setCreador(" ");
    agregadorService.enviarSolicitudEliminacion(solicitud);
    return "redirect:/colecciones";
  }

  @GetMapping("/colecciones/{idColeccion}/hechos/{idHecho}/solicitudEliminacion")
  public String mostrarFormularioSolicitud(@PathVariable String idColeccion, @PathVariable Long idHecho, Model model) {
    SolicitudEliminacionDTO solicitud = new SolicitudEliminacionDTO();
    solicitud.setIdHecho(idHecho);
    model.addAttribute("solicitudEliminacion", solicitud);
    model.addAttribute("hechoId", idHecho);
    model.addAttribute("idColeccion", idColeccion);
    return "coleccion/solicitudEliminacion";
  }

  @GetMapping("/colecciones/{idColeccion}/hechos/{idHecho}")
  public String getDetallesHecho(@PathVariable Long idHecho, @PathVariable String idColeccion, Model model) {
    HechoDetallesDTO hechoDetallesDTO = agregadorService.getDetallesHecho(idHecho);
    model.addAttribute("idColeccion", idColeccion);
    model.addAttribute("hecho", hechoDetallesDTO);
    return "coleccion/detallesHecho";
  }

  private ColeccionNuevaDTO mapearAFormulario(Coleccion coleccion) {
    ColeccionNuevaDTO form = new ColeccionNuevaDTO();

    form.setTitulo(coleccion.getTitulo());
    form.setDescripcion(coleccion.getDescripcion());
    form.setAlgoritmoConsenso(coleccion.getAlgoritmoConsenso());
    if (coleccion.getFuentes() != null) {
      List<FuenteNuevaDTO> fuentesDTO = new ArrayList<>();
      coleccion.getFuentes().forEach(f -> {
        FuenteNuevaDTO fd = new FuenteNuevaDTO();
        fd.setTipoFuente(f.getTipoFuente());
        fd.setUrl(f.getUrl());
        fuentesDTO.add(fd);
      });
      form.setFuentes(fuentesDTO);
    }

    if (coleccion.getCriterios() != null) {
      form.setCriterios(coleccion.getCriterios());
    } else {
      form.setCriterios(new ArrayList<>());
    }

    return form;
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/colecciones/nuevaColeccion")
  public String mostrarFormularioCreacion(Model model) {
    model.addAttribute("coleccionForm", new ColeccionNuevaDTO());
    model.addAttribute("esEdicion", false);
    model.addAttribute("accion", "/colecciones/crear");
    model.addAttribute("fuenteEstaticaUrl", fuenteEstaticaUrl);
    model.addAttribute("fuenteDinamicaUrl", fuenteDinamicaUrl);
    model.addAttribute("fuenteProxiAPIUrl", fuenteProxiAPIUrl);
    model.addAttribute("listaProvincias", agregadorService.obtenerProvincias());
    model.addAttribute("listaMunicipios", agregadorService.obtenerMunicipios());
    model.addAttribute("listaDepartamentos", agregadorService.obtenerDepartamentos());
    return "coleccion/formColeccion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/colecciones/{idColeccion}/editar")
  public String mostrarFormularioEdicion(@PathVariable String idColeccion, Model model) {
    Coleccion coleccionExistente = agregadorService.obtenerColeccionPorId(idColeccion);
    ColeccionNuevaDTO form = mapearAFormulario(coleccionExistente);

    model.addAttribute("coleccionForm", form);
    model.addAttribute("esEdicion", true);
    model.addAttribute("accion", "/colecciones/" + idColeccion + "/actualizar");
    model.addAttribute("fuenteEstaticaUrl", fuenteEstaticaUrl);
    model.addAttribute("fuenteDinamicaUrl", fuenteDinamicaUrl);
    model.addAttribute("fuenteProxiAPIUrl", fuenteProxiAPIUrl);
    model.addAttribute("listaProvincias", agregadorService.obtenerProvincias());
    model.addAttribute("listaMunicipios", agregadorService.obtenerMunicipios());
    model.addAttribute("listaDepartamentos", agregadorService.obtenerDepartamentos());
    return "coleccion/formColeccion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/colecciones/{idColeccion}/actualizar")
  public String actualizarColeccion(@PathVariable String idColeccion,
      @ModelAttribute("coleccionForm") ColeccionNuevaDTO coleccion,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    try {
      agregadorService.actualizarColeccion(idColeccion, coleccion);
      redirectAttributes.addFlashAttribute("success", "Colección actualizada correctamente.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
      return "redirect:/colecciones/" + idColeccion + "/editar";
    }

    return "redirect:/colecciones";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/colecciones/crear")
  public String crearColeccion(
      @ModelAttribute("coleccionForm") ColeccionNuevaDTO coleccionNueva,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    try {
      agregadorService.crearColeccion(coleccionNueva);
      redirectAttributes.addFlashAttribute("success", "Colección creada correctamente.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error al crear: " + e.getMessage());
      return "redirect:/colecciones/nuevaColeccion";
    }

    return "redirect:/colecciones";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/colecciones/{idColeccion}/eliminar")
  public String eliminarColeccion(@PathVariable String idColeccion) {
    agregadorService.eliminarColeccion(idColeccion);
    return "redirect:/colecciones";
  }

  @GetMapping("/colecciones/{idColeccion}/hechos")
  public String getHechosDeColeccion(@PathVariable String idColeccion, Model model,
      @ModelAttribute("filtros") FiltrosDTO filtros,
      @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
    ColeccionHechosDTO coleccionHechosDTO = agregadorService.getHechosColeccion(idColeccion, filtros, page);
    List<HechoPaginacionDTO> hechos = coleccionHechosDTO.getHechos().getData();
    Coleccion coleccion = agregadorService.obtenerColeccionPorId(idColeccion);
    String tituloMostrar = (coleccion != null) ? coleccion.getTitulo() : "Colección no encontrada";
    model.addAttribute("paginaActual", coleccionHechosDTO.getHechos().getCurrentPage());
    model.addAttribute("paginasTotales", coleccionHechosDTO.getHechos().getTotalPages());
    model.addAttribute("hechos", hechos);
    model.addAttribute("idColeccion", idColeccion);
    model.addAttribute("titulo", tituloMostrar);
    model.addAttribute("filtros", filtros);
    model.addAttribute("listaProvincias", agregadorService.obtenerProvincias());
    model.addAttribute("listaMunicipios", agregadorService.obtenerMunicipios());
    model.addAttribute("listaDepartamentos", agregadorService.obtenerDepartamentos());
    return "coleccion/hechosColeccion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/colecciones/{idColeccion}/crearEstadistica")
  public String crearEstadisticaColeccion(@PathVariable String idColeccion,
      @ModelAttribute("nuevaEstadistica") NuevaEstadisticaDTO nuevaEstadisticaDTO, BindingResult bindingResult,
      Model model, RedirectAttributes redirectAttributes) {
    try {
      estadisticaService.crearEstadistica(nuevaEstadisticaDTO);
      return "redirect:/colecciones";
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/colecciones/{idColeccion}/nuevaEstadistica";
    }
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/colecciones/{idColeccion}/nuevaEstadistica")
  public String mostrarFormulario(@PathVariable String idColeccion, Model model) {
    NuevaEstadisticaDTO nuevaEstadisticaDTO = new NuevaEstadisticaDTO();
    nuevaEstadisticaDTO.setUrlColeccion(agregadorUrl + "/colecciones/" + idColeccion);
    model.addAttribute("nuevaEstadistica", nuevaEstadisticaDTO);
    if (!model.containsAttribute("error")) {
      model.addAttribute("error", null);
    }
    return "coleccion/nuevaEstadistica";
  }

  @GetMapping("/colecciones")
  public String getColecciones(Model model, RedirectAttributes redirectAttributes) {
    List<Coleccion> colecciones = agregadorService.obtenerColecciones();
    model.addAttribute("colecciones", colecciones);
    return "coleccion/colecciones";
  }

  @GetMapping("/panel-control/solicitudesEliminacion/{idSolicitud}/hecho")
  public String detallesHechoSolicitudEliminacion(@PathVariable Long idSolicitud, Model model) {
    SolicitudEliminacionDetallesDTO solicitud = agregadorService.obtenerSolicitudEliminacion(idSolicitud);
    HechoDetallesDTO hecho = agregadorService.getDetallesHecho(solicitud.getIdHecho());
    model.addAttribute("hecho", hecho);
    model.addAttribute("esRevision", true);
    model.addAttribute("idSolicitud", solicitud.getId());
    model.addAttribute("idColeccion", "admin-view");
    return "coleccion/detallesHecho";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/solicitudesEliminacion/{idSolicitud}/aceptar")
  public String procesarAceptacionSolicitudEliminacion(@PathVariable Long idSolicitud) {
    agregadorService.aceptarSolicitudEliminacion(idSolicitud);
    return "redirect:/panel-control/solicitudesEliminacion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/solicitudesEliminacion/{idSolicitud}/rechazar")
  public String procesarRechazoSolicitudEliminacion(@PathVariable Long idSolicitud) {
    agregadorService.rechazarSolicitudEliminacion(idSolicitud);
    return "redirect:/panel-control/solicitudesEliminacion";
  }

  @GetMapping("/panel-control/solicitudesEliminacion/{idSolicitud}")
  public String verDetallesSolicitud(Model model, @PathVariable Long idSolicitud) {
    SolicitudEliminacionDetallesDTO solicitudEliminacionDetallesDTO = agregadorService
        .obtenerSolicitudEliminacion(idSolicitud);
    model.addAttribute("solicitud", solicitudEliminacionDetallesDTO);
    model.addAttribute("pendiente", solicitudEliminacionDetallesDTO.getEstadoActual() == "PENDIENTE");
    return "solicitudes/solicitudEliminacionDetalles";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/solicitudesEliminacion")
  public String mostrarSolicitudesEliminacion(Model model, @RequestParam(defaultValue = "1") int page,
      @RequestParam(required = false, defaultValue = "true") Boolean pendientes) {
    SolicitudEliminacionPaginadaDTO solicitudesPaginadoDTO = agregadorService.obtenerSolicitudesEliminacion(
        page,
        pendientes,
        false);
    model.addAttribute("subtitulo", "Gestiona los reportes de hechos realizados por usuarios.");
    model.addAttribute("noSolicitudesMensaje", "Buen trabajo! Todo está al día.");
    model.addAttribute("page", solicitudesPaginadoDTO.getCurrentPage());
    model.addAttribute("totalPages", solicitudesPaginadoDTO.getTotalPages());
    model.addAttribute("solicitudes", solicitudesPaginadoDTO.getData());
    model.addAttribute("pendientes", pendientes);
    return "solicitudes/solicitudesEliminacion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control")
  public String mostrarPanelControl(Model model) {
    ResumenActividadDTO resumenActividadDTO = agregadorService.obtenerResumenActividad();
    model.addAttribute("hechosTotales", resumenActividadDTO.getHechostotales());
    model.addAttribute("fuentesTotales", resumenActividadDTO.getFuentesTotales());
    model.addAttribute("solicitudesEliminacion", resumenActividadDTO.getSolicitudesEliminacion());

    List<EstadisticaDTO> estadisticas = estadisticaService.obtenerEstadisticas();
    model.addAttribute("estadisticas", estadisticas);
    return "panelControl";
  }

  @GetMapping("/solicitudes-usuario")
  public String mostrarSolicitudesEliminacionCreadasPor(Model model, @RequestParam(defaultValue = "1") int page,
      @RequestParam(required = false, defaultValue = "true") Boolean pendientes, HttpServletRequest request) {
    Object username = request.getSession().getAttribute("username");
    if (username != null) {
      SolicitudEliminacionPaginadaDTO solicitudesPaginadoDTO = agregadorService.obtenerSolicitudesEliminacion(
          page,
          pendientes,
          true);
      model.addAttribute("subtitulo", "Gestiona los reportes de hechos que has realizado.");
      model.addAttribute("noSolicitudesMensaje", "No has realizado ninguna solicitud de eliminación aún.");
      model.addAttribute("page", solicitudesPaginadoDTO.getCurrentPage());
      model.addAttribute("totalPages", solicitudesPaginadoDTO.getTotalPages());
      model.addAttribute("solicitudes", solicitudesPaginadoDTO.getData());
      model.addAttribute("pendientes", pendientes);
    } else {
      return "login";
    }
    return "solicitudes/solicitudesEliminacion";
  }
}