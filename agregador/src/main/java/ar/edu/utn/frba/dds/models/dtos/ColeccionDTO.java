package ar.edu.utn.frba.dds.models.dtos;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.util.Set;
import lombok.Data;

@Data
public class ColeccionDTO {
  private String titulo;
  private String descripcion;
  private Set<Fuente> fuentes;
  private IConsensoStrategy algoritmoConsenso;
  private Set<IFiltroStrategy> criterios;
}
