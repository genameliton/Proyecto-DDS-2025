package ar.edu.utn.frba.dds.utils;

import ar.edu.utn.frba.dds.entities.Hecho;
import java.util.Set;

public interface FuenteDeDatos {
  public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios);
}
