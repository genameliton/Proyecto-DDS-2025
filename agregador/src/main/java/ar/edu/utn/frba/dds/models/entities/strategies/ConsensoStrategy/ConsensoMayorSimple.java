package ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.Set;

@Entity
@DiscriminatorValue("mayoria_simple")
public class ConsensoMayorSimple extends IConsensoStrategy {
  @Override
  public Boolean cumpleConsenso(Hecho hecho, Set<Fuente> fuentes) {
    Integer mayoria = (fuentes.size() / 2) + 1;
    return cumpleConsensoBase(hecho, fuentes, mayoria);
  }

  @Override
  public TipoAlgoritmo getTipo() {
    return TipoAlgoritmo.MAYORIA_SIMPLE;
  }
}
