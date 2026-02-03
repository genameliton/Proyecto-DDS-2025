package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.CambioAlgoritmoDTO;
import ar.edu.utn.frba.dds.models.dtos.input.ColeccionDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.input.FiltroDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDetallesDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.input.SolicitudDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.PaginacionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.ResumenActividadDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudEliminacionDTOSalida;
import ar.edu.utn.frba.dds.models.entities.factories.FiltroStrategyFactory;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import ar.edu.utn.frba.dds.services.ColeccionService;
import ar.edu.utn.frba.dds.services.GeoToolsProcessorService;
import ar.edu.utn.frba.dds.services.SolicitudEliminacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
public class AgregadorController {
  private final SolicitudEliminacionService solicitudService;
  private final ColeccionService coleccionService;
  private final GeoToolsProcessorService geoService;

  public AgregadorController(SolicitudEliminacionService solicitudService, ColeccionService coleccionService,
      GeoToolsProcessorService geoService) {
    this.solicitudService = solicitudService;
    this.coleccionService = coleccionService;
    this.geoService = geoService;
  }

  // Panel control
  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/resumen")
  public ResponseEntity<ResumenActividadDTOSalida> getResumenActividad() {
    ResumenActividadDTOSalida resumenActividadDTO = coleccionService.getResumenActividad();
    return ResponseEntity.status(HttpStatus.OK).body(resumenActividadDTO);
  }
  // COLECCIONES

  // CRUD COLECCIONES
  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/colecciones")
  public ResponseEntity<ColeccionDTOSalida> createColeccion(@RequestBody ColeccionDTOEntrada dto) {
    ColeccionDTOSalida coleccionCreada = coleccionService.createColeccion(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(coleccionCreada);
  }

  @GetMapping("/colecciones")
  public List<ColeccionDTOSalida> getColecciones() {
    return coleccionService.getColeccionesDTO();
  }

  @GetMapping("/colecciones/{id}")
  public ColeccionDTOSalida getColeccion(@PathVariable String id) {
    return coleccionService.getColeccionDTO(id);
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PutMapping("/colecciones/{id}")
  public void updateColeccion(@PathVariable String id, @RequestBody ColeccionDTOEntrada dto) {
    coleccionService.updateColeccion(id, dto);
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @DeleteMapping("/colecciones/{id}")
  public void deleteColeccion(@PathVariable String id) {
    coleccionService.deleteColeccion(id);
  }

  @GetMapping("/colecciones/{id}/hechos")
  public PaginacionDTOSalida<HechoDTOSalida> getHechos(
      @PathVariable String id,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false, defaultValue = "false") Boolean curados,
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false) LocalDate fecha_acontecimiento_desde,
      @RequestParam(required = false) LocalDate fecha_acontecimiento_hasta,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String municipio,
      @RequestParam(required = false) String departamento) {
    Set<IFiltroStrategy> filtros = FiltroStrategyFactory.fromParams(
        categoria,
        fecha_acontecimiento_desde,
        fecha_acontecimiento_hasta,
        provincia,
        municipio,
        departamento);

    return coleccionService.getHechos(id, curados, page, filtros);
  }

  @PutMapping("/colecciones/{id}/algoritmo")
  public void updateAlgoritmoConsenso(@PathVariable String id, @RequestBody CambioAlgoritmoDTO algoritmoDTO) {
    coleccionService.updateAlgoritmoConsenso(id, algoritmoDTO);
  }

  @PostMapping("/colecciones/{id}/fuentes")
  public void addFuente(@PathVariable String id, @RequestBody FuenteDTO dto) {
    coleccionService.addFuente(id, dto);
  }

  @DeleteMapping("/colecciones/{id}/fuentes")
  public void removeFuente(@PathVariable String id, @RequestParam String fuenteId) {
    coleccionService.removeFuente(id, fuenteId);
  }

  @PostMapping("/colecciones/{id}/filtros")
  public ResponseEntity<String> addCriterio(
      @PathVariable String id,
      @RequestBody FiltroDTOEntrada dto) {
    IFiltroStrategy filtro = FiltroStrategyFactory.fromDTO(dto);
    coleccionService.addCriterio(id, filtro);
    return ResponseEntity.ok("Filtro agregado correctamente");
  }

  @PutMapping("/colecciones/normaliza")
  public void actualizarHechosCurados() {
    coleccionService.refrescarHechosCurados();
  }

  @PutMapping("/colecciones")
  public void actualiza() {
    coleccionService.refrescoFuentes();
  }

  // HECHOS
  @GetMapping("/hechos/{idHecho}")
  public ResponseEntity<HechoDetallesDTOSalida> obtenerHecho(
      @PathVariable Long idHecho) {
    HechoDetallesDTOSalida respuesta = coleccionService.getHechoDTO(idHecho);
    return ResponseEntity.ok(respuesta);
  }

  // SOLICITUDES DE ELIMINACION
  @PostMapping("/solicitudes")
  public ResponseEntity<String> agregarSolicitudEliminacion(@RequestBody SolicitudDTOEntrada dto) {
    solicitudService.createSolicitudEliminacion(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud creada");
  }

  @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
  @GetMapping("/solicitudes")
  public PaginacionDTOSalida<SolicitudEliminacionDTOSalida> getSolicitudesEliminacion(
      @RequestParam(required = false, defaultValue = "1") Integer page,
      @RequestParam(required = false, defaultValue = "true") Boolean pendientes,
      @RequestParam(required = false, defaultValue = "false") Boolean filterByCreator) {
    return solicitudService.getSolicitudesEliminacionDTO(page, pendientes, filterByCreator);
  }

  @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
  @GetMapping("/solicitudes/{id}")
  public SolicitudDTOSalida getSolicitudEliminacion(@PathVariable Long id) {
    return solicitudService.getSolicitudEliminacionDTO(id);
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PutMapping("/solicitudes/{id}/aceptar")
  public ResponseEntity<String> aceptarSolicitudEliminacion(@PathVariable Long id) {
    solicitudService.aceptarSolicitudEliminacion(id);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PutMapping("/solicitudes/{id}/denegar")
  public ResponseEntity<String> rechazarSolicitudEliminacion(
      @PathVariable Long id) {
    solicitudService.rechazarSolicitudEliminacion(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/ubicaciones/provincias")
  public ResponseEntity<List<String>> obtenerProvincias() {
    return ResponseEntity.ok(geoService.getNombresProvincias());
  }

  @GetMapping({ "/ubicaciones/departamentos", "/ubicaciones/municipios" })
  public ResponseEntity<List<String>> obtenerDepartamentos() {
    return ResponseEntity.ok(geoService.getNombresDepartamentos());
  }
}