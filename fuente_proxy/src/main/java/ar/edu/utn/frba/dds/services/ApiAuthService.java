package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.external.api.auth.LoginRequest;
import ar.edu.utn.frba.dds.models.dtos.external.api.auth.LoginResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ApiAuthService {

  private final WebClient webClient;
  @Getter
  private final String bearerToken;

  public ApiAuthService(
      @Value("${api.email}") String email,
      @Value("${api.password}") String password,
      @Value("${api.baseUrl}") String baseUrl
  ) {
    this.webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("Content-Type", "application/json")
        .build();

    this.bearerToken = auth(email, password);
  }

  private String auth(String email, String password) {
    LoginRequest request = new LoginRequest(email, password);
    try {
      LoginResponse response = webClient.post()
          .uri("/api/login")
          .bodyValue(request)
          .retrieve()
          .bodyToMono(LoginResponse.class)
          .block();

      if (response == null || response.isError()) {
        throw new IllegalStateException("Autenticación fallida");
      }

      return response.getData().getAccess_token();
    } catch (Exception e) {
      throw new IllegalStateException("Error al autenticar con la API", e);
    }
  }
}
