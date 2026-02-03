package ar.edu.utn.frba.dds.config;

import ar.edu.utn.frba.dds.filters.JwtAuthFilter;
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
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/error", "/error/**", "/media/**", "/actuator/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/hechos").permitAll()
            .requestMatchers(HttpMethod.GET, "/hechos/{id}").permitAll()
            .requestMatchers(HttpMethod.POST, "/hechos").permitAll()

            .requestMatchers(HttpMethod.GET, "/hechos/pendientes").authenticated()
            .requestMatchers(HttpMethod.PUT, "/hechos/*/aceptar").authenticated()
            .requestMatchers(HttpMethod.PUT, "/hechos/*/aceptar-con-sugerencias").authenticated()
            .requestMatchers(HttpMethod.PUT, "/hechos/*/rechazar").authenticated()
            .requestMatchers(HttpMethod.PUT, "/hechos/{id}").authenticated()

            .anyRequest().authenticated()
        )
        //Middleware
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
