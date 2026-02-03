package ar.edu.utn.frba.dds.models.entities.utils;

import java.util.List;

public abstract class AbstractConverter<E, D, O> {
  public abstract E fromDTO(D dto);
  public abstract O fromEntity(E entity);

  public List<O> fromEntity(List<E> entities) {
    if (entities == null) return null;
    return entities.stream().map(ent -> fromEntity(ent)).toList();
  }
}
