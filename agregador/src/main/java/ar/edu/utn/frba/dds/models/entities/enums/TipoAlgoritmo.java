package ar.edu.utn.frba.dds.models.entities.enums;

import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.ConsensoAbsoluto;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.ConsensoMayorSimple;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.ConsensoMultiplesMenciones;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;

public enum TipoAlgoritmo {
  MULTIPLES_MENCIONES,
  MAYORIA_SIMPLE,
  ABSOLUTO;

  public IConsensoStrategy getStrategy() {
    return switch (this) {
      case MULTIPLES_MENCIONES -> new ConsensoMultiplesMenciones();
      case MAYORIA_SIMPLE -> new ConsensoMayorSimple();
      case ABSOLUTO -> new ConsensoAbsoluto();
    };
  }
}
