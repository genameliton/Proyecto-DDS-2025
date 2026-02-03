package ar.edu.utn.frba.dds.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GraphQLClientConfig {
  @Value("${spring.graphql.client.url}")
  private String graphqlServiceUrl;

  @Bean
  public HttpGraphQlClient httpGraphQlClient() {
    WebClient webClient = WebClient.builder()
        .baseUrl(graphqlServiceUrl)
        // Si necesitas autorización (ej. Bearer Token)
        //.defaultHeader("Authorization", "Bearer TU_TOKEN_DE_API")
        .build();

    return HttpGraphQlClient.builder(webClient).build();
  }

}
