package ar.edu.utn.frba.dds.config;

import static org.springframework.http.HttpStatus.CONFLICT;
import ar.edu.utn.frba.dds.models.AuthResponseDTO;
import ar.edu.utn.frba.dds.models.RolesPermisosDTO;
import ar.edu.utn.frba.dds.services.WebApiCallerService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class AuthProvider implements AuthenticationProvider {
  private final WebApiCallerService webApiCallerService;
  private final WebClient webClient;
  private final RestTemplate restTemplate;
  private final String authServiceUrl;

  public AuthProvider(
      WebApiCallerService webApiCallerService,
      WebClient.Builder webClientBuilder,
      RestTemplate restTemplate,
      @Value("${auth.service.url}") String authServiceUrl) {
    this.webApiCallerService = webApiCallerService;
    this.webClient = webClientBuilder.build();
    this.restTemplate = restTemplate;
    this.authServiceUrl = authServiceUrl;
  }

  public AuthResponseDTO login(String username, String password) {
    try {
      Map<String, String> body = new HashMap<>();
      body.put("username", username);
      body.put("password", password);
      ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(
          authServiceUrl + "/login",
          body,
          AuthResponseDTO.class);
      return response.getBody();
    } catch (WebClientResponseException e) {
      if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        return null;
      }
      throw new RuntimeException("Error en el servicio de autenticación: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new RuntimeException("Error de conexión con el servicio de autenticación: " + e.getMessage(), e);
    }
  }

  public AuthResponseDTO loginOAuth(String username, String provider) {
    Map<String, String> body = new HashMap<>();
    body.put("username", username);
    body.put("provider", provider);

    return webClient.post()
        .uri(authServiceUrl + "/oauth-login")
        .bodyValue(body)
        .retrieve()
        .bodyToMono(AuthResponseDTO.class)
        .block();
  }

  public Boolean register(String username, String password) {
    Map<String, String> body = new HashMap<>();
    body.put("username", username);
    body.put("password", password);
    ResponseEntity<Void> response = restTemplate.postForEntity(
        authServiceUrl + "/register",
        body,
        Void.class);
    if (response.getStatusCode() == HttpStatus.OK) {
      return true;
    } else if (response.getStatusCode() == CONFLICT) {
      return false;
    }
    return false;
  }

  public RolesPermisosDTO getRolesPermisos(String accessToken) {
    try {
      RolesPermisosDTO response = webApiCallerService.getWithAuth(
          authServiceUrl + "/user/roles-permisos",
          accessToken,
          RolesPermisosDTO.class);
      return response;
    } catch (Exception e) {
      throw new RuntimeException("Error al obtener roles y permisos: " + e.getMessage(), e);
    }
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    try {
      AuthResponseDTO authResponse = login(username, password);

      if (authResponse == null) {
        throw new BadCredentialsException("Usuario o contraseña inválidos");
      }

      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
      HttpServletRequest request = attributes.getRequest();

      request.getSession().setAttribute("accessToken", authResponse.getAccessToken());
      request.getSession().setAttribute("refreshToken", authResponse.getRefreshToken());
      request.getSession().setAttribute("username", username);

      RolesPermisosDTO rolesPermisos = getRolesPermisos(authResponse.getAccessToken());

      request.getSession().setAttribute("rol", rolesPermisos.getRol());
      request.getSession().setAttribute("permisos", rolesPermisos.getPermisos());

      List<GrantedAuthority> authorities = new ArrayList<>();
      rolesPermisos.getPermisos().forEach(permiso -> {
        authorities.add(new SimpleGrantedAuthority(permiso.name()));
      });
      authorities.add(new SimpleGrantedAuthority("ROLE_" + rolesPermisos.getRol().name()));

      return new UsernamePasswordAuthenticationToken(username, password, authorities);
    } catch (RuntimeException e) {
      throw new BadCredentialsException("Error en el sistema de autenticación: " + e.getMessage());
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
