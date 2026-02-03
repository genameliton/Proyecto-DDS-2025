package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.ColeccionGQLDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDetallesDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.PaginacionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudEliminacionDTOSalida;
import ar.edu.utn.frba.dds.services.ColeccionService;
import ar.edu.utn.frba.dds.services.SolicitudEliminacionService;

import ar.edu.utn.frba.dds.models.dtos.input.graphql.HechosFiltroEntrada;

import ar.edu.utn.frba.dds.models.entities.factories.FiltroStrategyFactory;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.graphql.data.method.annotation.Argument;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AgregadorGQLController {
  private final SolicitudEliminacionService solicitudService;
  private final ColeccionService coleccionService;

  public AgregadorGQLController(SolicitudEliminacionService solicitudService, ColeccionService coleccionService) {
    this.solicitudService = solicitudService;
    this.coleccionService = coleccionService;
  }

  @QueryMapping

  public List<ColeccionDTOSalida> colecciones() {
    return coleccionService.getColeccionesDTO();
  }

  @QueryMapping
  public ColeccionGQLDTOSalida coleccion(@Argument String id,
      @Argument Integer page,
      @Argument Boolean curados,
      @Argument HechosFiltroEntrada filtro) {
    Boolean curadosFinal = (curados == null) ? false : curados;
    Set<IFiltroStrategy> filtros = null;

    if (filtro != null) {
      filtros = FiltroStrategyFactory.fromParams(
          filtro.getCategoria(),
          (filtro.getFecha_acontecimiento_desde() != null) ? LocalDate.parse(filtro.getFecha_acontecimiento_desde())
              : null,
          (filtro.getFecha_acontecimiento_hasta() != null) ? LocalDate.parse(filtro.getFecha_acontecimiento_hasta())
              : null,
          filtro.getProvincia(),
          filtro.getMunicipio(),
          filtro.getDepartamento());
    }
    return coleccionService.getColeccionOutputDTO(id, curadosFinal, page, filtros);
  }

  @QueryMapping

  public HechoDetallesDTOSalida hecho(
      @Argument Long id) {
    return coleccionService.getHechoDTO(id);
  }

  // @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
  @QueryMapping
  public PaginacionDTOSalida<SolicitudEliminacionDTOSalida> solicitudes(
      @Argument Integer page,
      @Argument Boolean pendientes,
      @Argument Boolean filterByCreator) {
    return solicitudService.getSolicitudesEliminacionDTO(page, pendientes, filterByCreator);
  }

  // @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
  @QueryMapping
  public SolicitudDTOSalida solicitud(
      @Argument Long id) {
    return solicitudService.getSolicitudEliminacionDTO(id);
  }

  // Mutations
  // TODO implementar en vista mutations
}