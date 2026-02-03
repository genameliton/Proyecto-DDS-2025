package ar.edu.utn.frba.dds.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
  @Value("${spring.graphql.client.url}")
  String urlAgregador;
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }
  @Bean
  public HttpGraphQlClient gqlAgregadorClient(WebClient.Builder webClientBuilder) {
    WebClient webClient = webClientBuilder
        .baseUrl(urlAgregador)
        .build();

    // 2. Construye tu cliente GraphQL CON ESE WebClient
    return HttpGraphQlClient.builder(webClient).build();
  }

}
