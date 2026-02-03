package ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.Set;

@Entity
@DiscriminatorValue("multiples_menciones")
public class ConsensoMultiplesMenciones extends IConsensoStrategy {
  @Override
  public Boolean cumpleConsenso(Hecho hecho, Set<Fuente> fuentes) {
    return cumpleConsensoBase(hecho, fuentes, 2);
  }

  @Override
  public TipoAlgoritmo getTipo() {
    return TipoAlgoritmo.MULTIPLES_MENCIONES;
  }
}
