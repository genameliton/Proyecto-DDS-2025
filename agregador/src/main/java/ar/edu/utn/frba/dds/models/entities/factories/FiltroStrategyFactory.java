package ar.edu.utn.frba.dds.models.entities.factories;
import ar.edu.utn.frba.dds.models.dtos.input.FiltroDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroCategoria;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroDepartamento;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFechaAcontecimiento;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFechaReporte;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFuente;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroMunicipio;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroProvincia;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class FiltroStrategyFactory {
  public static IFiltroStrategy fromDTO(FiltroDTOEntrada dto) {
  try {
    TipoFiltro tipoFiltro = TipoFiltro.valueOf(dto.getTipoFiltro());
    return switch (tipoFiltro) {
      case FILTRO_CATEGORIA -> new FiltroCategoria(dto.getValor());
      case FILTRO_FECHA_ACONTECIMIENTO ->
          new FiltroFechaAcontecimiento(dto.getFechaInicio().atStartOfDay(), dto.getFechaFin().atStartOfDay());
      case FILTRO_FECHA_REPORTE -> new FiltroFechaReporte(dto.getFechaInicio().atStartOfDay(), dto.getFechaFin().atStartOfDay());
      case FILTRO_PROVINCIA -> new FiltroProvincia(dto.getValor());
      case FILTRO_DEPARTAMENTO -> new FiltroDepartamento(dto.getValor());
      case FILTRO_MUNICIPIO -> new FiltroMunicipio(dto.getValor());
      case FILTRO_FUENTE -> new FiltroFuente(dto.getTipoFuente());
    };
  } catch (Exception e) {
    throw new IllegalArgumentException("Tipo de filtro " + dto.getTipoFiltro() + " no soportada");
  }
  }

  public static Set<IFiltroStrategy> fromParams(
      String categoria,
      LocalDate fechaAcontecimientoDesde,
      LocalDate fechaAcontecimientoHasta,
      String provincia,
      String municipio,
      String departamento
  ) {

    Set<IFiltroStrategy> filtros = new HashSet<>();

    if (categoria != null && !categoria.isBlank())
      filtros.add(new FiltroCategoria(categoria));

    if (fechaAcontecimientoDesde != null || fechaAcontecimientoHasta != null)  {

      if(fechaAcontecimientoHasta == null) {
        filtros.add(new FiltroFechaAcontecimiento(fechaAcontecimientoDesde.atStartOfDay(), LocalDateTime.now()));
      } else if(fechaAcontecimientoDesde == null) {
        filtros.add(new FiltroFechaAcontecimiento( LocalDateTime.MIN, fechaAcontecimientoHasta.atStartOfDay()));
      } else {
        filtros.add(new FiltroFechaAcontecimiento(fechaAcontecimientoDesde.atStartOfDay(), fechaAcontecimientoHasta.atStartOfDay()));
      }
    }
    if (provincia != null && !provincia.isBlank()) {
      filtros.add(new FiltroProvincia(provincia));
    }

    if (municipio != null && !municipio.isBlank()) {
      filtros.add(new FiltroMunicipio(municipio));
    }

    if (departamento != null && !departamento.isBlank()) {
      filtros.add(new FiltroDepartamento(departamento));
    }

    return filtros;
  }
}