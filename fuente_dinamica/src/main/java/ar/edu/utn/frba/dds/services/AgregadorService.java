package ar.edu.utn.frba.dds.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class AgregadorService {

    @Value("${agregador.service.url}")
    private String agregadorBaseUrl;

    private final WebClient webClient;

    public AgregadorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Async
    public void notificarCambios() {
        String url = agregadorBaseUrl + "/fuentes/refrescar-dinamica";

        log.info("NOTIFICADOR: Enviando señal a: {}", url);

        try {
            webClient.post()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe(
                            response -> log.info("NOTIFICADOR: Éxito."),
                            error -> log.error("NOTIFICADOR: Error: {}", error.getMessage()));
        } catch (Exception e) {
            log.error("NOTIFICADOR: Falló el intento.", e);
        }
    }
}