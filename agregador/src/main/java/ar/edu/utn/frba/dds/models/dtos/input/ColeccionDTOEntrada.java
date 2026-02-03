package ar.edu.utn.frba.dds.models.dtos.input;

import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import java.util.Set;
import lombok.Data;

@Data
public class ColeccionDTOEntrada {
  private String titulo;
  private String descripcion;
  private Set<FuenteDTO> fuentes;
  private String algoritmoConsenso;
  private Set<FiltroDTOEntrada> criterios;
}
