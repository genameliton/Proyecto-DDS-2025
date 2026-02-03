package ar.edu.utn.frba.dds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            // modificar esto
            // Permitir GET libremente
            .requestMatchers(HttpMethod.GET, "/colecciones/**", "/actuator/**", "/graphql/**", "/hechos/**",
                "/ubicaciones/**")
            .permitAll()
            .requestMatchers(HttpMethod.POST, "/solicitudes", "/graphql/**", "/fuentes/refrescar-dinamica").permitAll()
            // Requerir autenticación para POST y PUT
            .requestMatchers(HttpMethod.POST, "/colecciones/**").authenticated()
            .requestMatchers(HttpMethod.PUT, "/colecciones/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/colecciones/**").authenticated()
            .anyRequest().authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
