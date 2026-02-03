package ar.edu.utn.frba.dds.models.events;

import java.util.List;

public record FuentesAProcesarEvent(String coleccionId, List<String> fuenteIds) {
}