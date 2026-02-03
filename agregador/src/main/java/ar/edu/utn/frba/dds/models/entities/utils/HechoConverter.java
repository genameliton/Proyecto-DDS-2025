package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDetallesDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.MultimediaDTOSalida;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Lugar;
import ar.edu.utn.frba.dds.models.entities.Multimedia;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.Formato;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frba.dds.services.GeoToolsProcessorService;
import org.springframework.stereotype.Component;

@Component
public class HechoConverter {
  private final GeoToolsProcessorService geoToolsProcessorService;

  public HechoConverter(GeoToolsProcessorService geoToolsProcessorService) {
    this.geoToolsProcessorService = geoToolsProcessorService;
  }

  public Hecho fromDTO(HechoDTOEntrada dto, TipoFuente tipoFuente) {
    Ubicacion ubicacion = new Ubicacion();
    ubicacion.setLatitud(dto.getLatitud());
    ubicacion.setLongitud(dto.getLongitud());
    ubicacion.setLugar(obtenerLugar(ubicacion));
    Origen origenExistente = new Origen();
    origenExistente.setTipo(tipoFuente);
    if (dto.getAutor() != null) {
      origenExistente.setAutor(dto.getAutor());
    } else {
      origenExistente.setAutor(null);
    }

    Hecho hecho = new Hecho();
    hecho.setIdExterno(dto.getId());
    hecho.setTitulo(dto.getTitulo());
    hecho.setDescripcion(dto.getDescripcion());
    hecho.setCategoria(dto.getCategoria());
    hecho.setUbicacion(ubicacion);
    hecho.setFechaAcontecimiento(dto.getFechaHecho());
    hecho.setFechaCarga(dto.getCreatedAt());
    hecho.setOrigen(origenExistente);
    if (dto.getMultimedia() != null) {
      List<Multimedia> listaMultimedia = new ArrayList<>();
      dto.getMultimedia().forEach(multimediaDTOInput -> {
        Multimedia multimedia = new Multimedia();
        multimedia.setHecho(hecho);
        multimedia.setNombre(multimediaDTOInput.getNombre());
        multimedia.setRuta(multimediaDTOInput.getRuta());
        try {
          Formato formato = Formato.valueOf(multimediaDTOInput.getFormato().toUpperCase());
          multimedia.setFormato(formato);
        } catch (Exception e) {
          throw new IllegalArgumentException("Tipo de formato " + multimediaDTOInput + " no soportado");
        }
        listaMultimedia.add(multimedia);
      });

      hecho.setMultimedia(listaMultimedia);
    }
    return hecho;
  }

  public Lugar obtenerLugar(Ubicacion ubicacion) {
    Lugar lugarLocal = geoToolsProcessorService.buscarPorPoligono(ubicacion.getLatitud(), ubicacion.getLongitud());
    if (lugarLocal != null) {
      return lugarLocal;
    }
    return null;
  }

  public HechoDTOSalida fromEntity(Hecho hecho) {
    HechoDTOSalida hechoDTOSalida = new HechoDTOSalida();
    hechoDTOSalida.setId(hecho.getId());
    hechoDTOSalida.setTitulo(hecho.getTitulo());
    if (hecho.getUbicacion() != null && hecho.getUbicacion().getLugar() != null) {
      if (hecho.getUbicacion().getLugar().getDepartamento() != null) {
        hechoDTOSalida.setDepartamento(hecho.getUbicacion().getLugar().getDepartamento());
      }
      if (hecho.getUbicacion().getLugar().getMunicipio() != null) {
        hechoDTOSalida.setMunicipio(hecho.getUbicacion().getLugar().getMunicipio());
      }
      if (hecho.getUbicacion().getLugar().getProvincia() != null) {
        hechoDTOSalida.setProvincia(hecho.getUbicacion().getLugar().getProvincia());
      }
    }
    hechoDTOSalida.setCategoria(hecho.getCategoria());
    if (hecho.getUbicacion() != null) {
      hechoDTOSalida.setLatitud(hecho.getUbicacion().getLatitud());
      hechoDTOSalida.setLongitud(hecho.getUbicacion().getLongitud());
    }
    hechoDTOSalida.setTipoFuente(hecho.getOrigen().getTipo().toString());
    hechoDTOSalida.setNombreAutor(hecho.getOrigen().getAutor());
    hechoDTOSalida.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    return hechoDTOSalida;
  }

  public HechoDetallesDTOSalida fromEntityDetails(Hecho hecho) {
    HechoDetallesDTOSalida hechoDetallesDTOSalida = new HechoDetallesDTOSalida();
    hechoDetallesDTOSalida.setId(hecho.getId());
    if (hecho.getUbicacion().getLugar() != null) {
      if (hecho.getUbicacion().getLugar().getDepartamento() != null) {
        hechoDetallesDTOSalida.setDescripcion(hecho.getUbicacion().getLugar().getDepartamento());
      }
      if (hecho.getUbicacion().getLugar().getMunicipio() != null) {
        hechoDetallesDTOSalida.setMunicipio(hecho.getUbicacion().getLugar().getMunicipio());
      }
      if (hecho.getUbicacion().getLugar().getProvincia() != null) {
        hechoDetallesDTOSalida.setProvincia(hecho.getUbicacion().getLugar().getProvincia());
      }
    }
    hechoDetallesDTOSalida.setLatitud(hecho.getUbicacion().getLatitud());
    hechoDetallesDTOSalida.setLongitud(hecho.getUbicacion().getLongitud());
    hechoDetallesDTOSalida.setCategoria(hecho.getCategoria());
    hechoDetallesDTOSalida.setFechaCarga(hecho.getFechaCarga());
    hechoDetallesDTOSalida.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    hechoDetallesDTOSalida.setDescripcion(hecho.getDescripcion());
    hechoDetallesDTOSalida.setTitulo(hecho.getTitulo());
    hechoDetallesDTOSalida.setTipoOrigen(hecho.getOrigen().getTipo());
    hechoDetallesDTOSalida.setNombreAutor(hecho.getOrigen().getAutor());
    if (hecho.getMultimedia() != null) {
      List<MultimediaDTOSalida> listaMultimedia = new ArrayList<>();
      hecho.getMultimedia().forEach(multimedia -> {
        MultimediaDTOSalida multimediaDTOOutput = new MultimediaDTOSalida();
        multimediaDTOOutput.setNombre(multimedia.getNombre());
        multimediaDTOOutput.setRuta(multimedia.getRuta());
        multimediaDTOOutput.setFormato(multimedia.getFormato().toString());
        listaMultimedia.add(multimediaDTOOutput);
      });
      hechoDetallesDTOSalida.setMultimedia(listaMultimedia);
    }
    return hechoDetallesDTOSalida;
  }
}
