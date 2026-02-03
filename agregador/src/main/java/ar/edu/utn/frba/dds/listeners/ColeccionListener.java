package ar.edu.utn.frba.dds.listeners;

import ar.edu.utn.frba.dds.models.events.FuentesAProcesarEvent;
import ar.edu.utn.frba.dds.services.ProcesadorFuentesService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ColeccionListener {

    private final ProcesadorFuentesService procesadorFuentesService;

    public ColeccionListener(ProcesadorFuentesService procesadorFuentesService) {
        this.procesadorFuentesService = procesadorFuentesService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void procesarFuentes(FuentesAProcesarEvent event) {
        if (event.fuenteIds() == null || event.fuenteIds().isEmpty())
            return;

        event.fuenteIds().forEach(fuenteId -> procesadorFuentesService.procesarFuenteAsync(fuenteId,
                event.coleccionId()));
    }
}