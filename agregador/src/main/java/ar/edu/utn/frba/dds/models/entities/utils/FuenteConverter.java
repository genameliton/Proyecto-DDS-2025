package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.dtos.output.FuenteDTOSalida;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import org.springframework.stereotype.Component;

@Component
public class FuenteConverter {

  public Fuente fromDTO(FuenteDTO fuenteDTO) {
    if (fuenteDTO.getTipoFuente() == null || fuenteDTO.getUrl() == null) {
      throw new IllegalArgumentException("La url y/o el tipo de fuente estan vacios");
    }
    TipoFuente tipoFuente = TipoFuente.valueOf(fuenteDTO.getTipoFuente().toUpperCase());
    Fuente fuente;
    switch (tipoFuente) {
      case PROXY_METAMAPA:
      case PROXY_API:
      case DINAMICA:
      case ESTATICA:
        fuente = new Fuente(fuenteDTO.getUrl(), tipoFuente);
        break;
      default:
        throw new IllegalArgumentException("Tipo de fuente " + fuenteDTO.getTipoFuente() + " no soportado");
    }
    return fuente;
  }

  public FuenteDTOSalida fromEntity(Fuente f) {
    return new FuenteDTOSalida(f.getId(), f.getTipoFuente(), f.getUrl());
  }
}
