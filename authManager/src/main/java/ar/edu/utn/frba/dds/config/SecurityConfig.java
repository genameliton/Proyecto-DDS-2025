package ar.edu.utn.frba.dds.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
  private final JwtAuthFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final CustomAuthenticationEntryPoint customEntryPoint;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(req ->
            req.requestMatchers("/auth/**", "/actuator/**")
                .permitAll()
                .anyRequest()
                .authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(customEntryPoint)
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(logout ->
            logout.logoutUrl("/auth/logout")
                .addLogoutHandler(((request, response, authentication) -> {
                  String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                }
                ))
                .logoutSuccessHandler(((request, response, authentication) -> {
                  SecurityContextHolder.clearContext();
                }))
        );


    return http.build();
  }
}
