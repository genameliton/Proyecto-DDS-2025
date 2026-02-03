package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.EstadoColeccion;
import ar.edu.utn.frba.dds.models.entities.utils.HechoConverter;
import ar.edu.utn.frba.dds.models.repositories.IColeccionRepository;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechoRepository;
import ar.edu.utn.frba.dds.models.repositories.IOrigenRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class ProcesadorFuentesService {

    private final IHechoRepository hechoRepository;
    private final IOrigenRepository origenRepo;
    private final IFuenteRepository fuenteRepository;
    private final IColeccionRepository coleccionRepository;
    private final HechoConverter hechoConverter;
    private final WebClient webClient;

    public ProcesadorFuentesService(IHechoRepository hechoRepository, IOrigenRepository origenRepo,
            IFuenteRepository fuenteRepository, IColeccionRepository coleccionRepository,
            HechoConverter hechoConverter, WebClient.Builder webClientBuilder) {
        this.hechoRepository = hechoRepository;
        this.origenRepo = origenRepo;
        this.fuenteRepository = fuenteRepository;
        this.coleccionRepository = coleccionRepository;
        this.hechoConverter = hechoConverter;
        this.webClient = webClientBuilder.build();
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void procesarFuenteAsync(String fuenteId, String coleccionId) {
        try {
            Fuente fuente = fuenteRepository.findById(fuenteId).orElseThrow();
            log.info("ASYNC: Sincronizando fuente: {}", fuente.getUrl());

            Set<Hecho> hechosEntrantes = fuente.obtenerHechosRefrescados(hechoConverter, webClient);
            Set<Hecho> hechosActuales = fuente.getHechos();

            Map<Long, Hecho> mapaActuales = hechosActuales.stream()
                    .filter(h -> h.getIdExterno() != null)
                    .collect(Collectors.toMap(Hecho::getIdExterno, Function.identity(), (a, b) -> a));

            Set<Hecho> listaFinal = new HashSet<>();

            log.info("Procesando {} hechos entrantes contra {} hechos actuales.",
                    hechosEntrantes.size(), hechosActuales.size());

            for (Hecho nuevo : hechosEntrantes) {
                Long idExt = nuevo.getIdExterno();

                if (idExt != null && mapaActuales.containsKey(idExt)) {
                    Hecho existente = mapaActuales.get(idExt);

                    if (sonDiferentes(existente, nuevo)) {
                        existente.setTitulo(nuevo.getTitulo());
                        existente.setDescripcion(nuevo.getDescripcion());
                        existente.setCategoria(nuevo.getCategoria());
                        existente.setFechaAcontecimiento(nuevo.getFechaAcontecimiento());

                        if (ubicacionDiferente(existente, nuevo)) {
                            existente.setUbicacion(nuevo.getUbicacion());
                        }

                        actualizarMultimedia(existente, nuevo);
                    }

                    listaFinal.add(existente);
                    mapaActuales.remove(idExt);
                } else {
                    origenRepo.findFirstByTipoAndAutor(nuevo.getOrigen().getTipo(), nuevo.getOrigen().getAutor())
                            .ifPresent(nuevo::setOrigen);
                    hechoRepository.buscarCategoriaNormalizada(nuevo.getCategoria())
                            .ifPresent(nuevo::setCategoria);

                    listaFinal.add(nuevo);
                }
            }

            for (Hecho hBorrar : mapaActuales.values()) {
                fuente.getHechos().remove(hBorrar);
            }

            log.info("Guardando {} hechos finales para la fuente {}.", listaFinal.size(), fuenteId);
            hechoRepository.saveAll(listaFinal);

            fuente.setHechos(listaFinal);
            fuenteRepository.save(fuente);

            if (coleccionId != null) {
                Coleccion coleccion = coleccionRepository.findById(coleccionId).orElseThrow();
                coleccion.setEstado(EstadoColeccion.DISPONIBLE);
                coleccion.refrescarHechosCurados();
                coleccionRepository.save(coleccion);
                log.info("ASYNC: Colección {} marcada como DISPONIBLE", coleccionId);
            }
            log.info("ASYNC: Procesamiento de fuente {} completado.", fuenteId);

        } catch (Exception e) {
            log.error("ASYNC ERROR: Falló procesamiento de fuente {}", fuenteId, e);
        }
    }

    private boolean sonDiferentes(Hecho existente, Hecho nuevo) {
        return !Objects.equals(existente.getTitulo(), nuevo.getTitulo()) ||
                !Objects.equals(existente.getDescripcion(), nuevo.getDescripcion()) ||
                !Objects.equals(existente.getCategoria(), nuevo.getCategoria()) ||
                !Objects.equals(existente.getFechaAcontecimiento(), nuevo.getFechaAcontecimiento()) ||
                ubicacionDiferente(existente, nuevo) ||
                (nuevo.getMultimedia() != null && !nuevo.getMultimedia().isEmpty());
    }

    private boolean ubicacionDiferente(Hecho existente, Hecho nuevo) {
        if (existente.getUbicacion() == null && nuevo.getUbicacion() == null)
            return false;
        if (existente.getUbicacion() == null || nuevo.getUbicacion() == null)
            return true;

        return !Objects.equals(existente.getUbicacion().getLatitud(), nuevo.getUbicacion().getLatitud()) ||
                !Objects.equals(existente.getUbicacion().getLongitud(), nuevo.getUbicacion().getLongitud());
    }

    private void actualizarMultimedia(Hecho existente, Hecho nuevo) {
        existente.getMultimedia().clear();

        if (nuevo.getMultimedia() != null) {
            nuevo.getMultimedia().forEach(m -> {
                m.setHecho(existente);
                existente.getMultimedia().add(m);
            });
        }
    }
}