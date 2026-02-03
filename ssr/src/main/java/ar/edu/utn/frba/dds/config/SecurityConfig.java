package ar.edu.utn.frba.dds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {
  private final OAuth2LoginSuccessHandler successHandler;

  public SecurityConfig(OAuth2LoginSuccessHandler successHandler) {
    this.successHandler = successHandler;
  }

  @Bean
  public AuthenticationManager authManager(HttpSecurity http, AuthProvider provider) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
        .authenticationProvider(provider)
        .build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests(
            auth -> auth
                // Recursos estáticos y login público
                .requestMatchers("/", "/home/**","/registro", "/registrar", "/login", "/css/**", "/js/**", "/media/**", "/oauth2/**", "/crear-hecho", "/subir-hecho", "/actuator/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/colecciones").permitAll()
                //new config
                .requestMatchers(HttpMethod.GET, "/colecciones/*/hechos/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/solicitarEliminacion").permitAll()
                .anyRequest().authenticated()
        )
        .oauth2Login(oauth -> oauth
            .loginPage("/login")
            .successHandler(successHandler))
        .exceptionHandling(Customizer.withDefaults())
        .formLogin(
            form -> form
                .loginPage("/login")    // tu template de login
                .permitAll()
                .defaultSuccessUrl("/colecciones", true) // redirigir tras login exitoso
        )
        .logout(
            logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // redirigir tras logout
                .permitAll()
        )
        .exceptionHandling(ex -> ex
            // Usuario no autenticado → redirigir a login
        .authenticationEntryPoint((request, response, authException) ->
            response.sendRedirect("/login?unauthorized")
        )
        // Usuario autenticado pero sin permisos → redirigir a página de error
        .accessDeniedHandler((request, response, accessDeniedException) ->
            response.sendRedirect("/403")
        )
        );
    return http.build();
  }
}
