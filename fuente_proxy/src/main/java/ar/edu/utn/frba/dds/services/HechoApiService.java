package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.external.api.hecho.HechoDTO;
import ar.edu.utn.frba.dds.models.dtos.external.api.hecho.HechosPagDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HechoApiService {
  private final WebClient webClient;

  public HechoApiService(
      @Value("${api.baseUrl}") String baseUrl,
      ApiAuthService authService
  ) {
    String token = authService.getBearerToken();
    this.webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("Authorization", "Bearer " + token)
        .build();
  }

  private Mono<HechosPagDTO> getHechosPag(Integer page, Integer perPage) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/api/desastres")
            .queryParam("page", page)
            .queryParam("per_page", perPage)
            .build())
        .retrieve()
        .bodyToMono(HechosPagDTO.class);
  }

  public Flux<HechoDTO> getHechos() {
    return getHechosPag(1, 100)
        .flatMapMany(firstPage -> {
          int lastPage = firstPage.getLastPage();

          return Flux.range(1, lastPage)
              .flatMap(pagina -> getHechosPag(pagina, 100))
              .flatMap(response -> Flux.fromIterable(response.getData()));
        });
  }


  public Mono<HechoDTO> getHechoById(Integer id) {
    return webClient.get()
        .uri("/api/desastres/{id}", id)
        .retrieve()
        .bodyToMono(HechoDTO.class);
  }
}