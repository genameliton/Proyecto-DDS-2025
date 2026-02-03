package ar.edu.utn.frba.dds.externalApi;

import ar.edu.utn.frba.dds.models.dtos.SpamDetectorRequestDTO;
import ar.edu.utn.frba.dds.models.dtos.SpamDetectorResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class SpamApi {
  private String url = "https://api.oopspam.com/v1/spamdetection";
  private WebClient webClient;

  public boolean esSpam(String texto) {
    Boolean spam = false;

    SpamDetectorResponseDTO spamDetectorResponse = sendSpamDetectorRequest(texto);

    if (spamDetectorResponse.getDetails().getSpamWordsNum() > 0) {
      spam = true;
    }
    return spam;
  }

  public SpamDetectorResponseDTO sendSpamDetectorRequest(String content) {
    this.webClient = WebClient.builder().baseUrl(url).build();
    //Mapear respuesta a un DTO
    Mono<SpamDetectorResponseDTO> response = webClient
        .post()
        .header("X-Api-Key", "3p0mc2OfA8IkAcD1ZQHnwTVEHmVKiDQE7ljqGSig")
        .bodyValue(new SpamDetectorRequestDTO(content))
        .retrieve()
        .bodyToMono(SpamDetectorResponseDTO.class);
    return response.block();
  }
}
