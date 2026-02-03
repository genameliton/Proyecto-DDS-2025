package ar.edu.utn.frba.dds.config;

import ar.edu.utn.frba.dds.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  )
      throws ServletException, IOException {

    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    // 1. Verificar el encabezado
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String token = authHeader.substring(7);
    String username = null;
    String role = null;

    try {
      // 2. Extraer información del token de forma segura
      username = jwtService.extractUsername(token);
      role = jwtService.extractRol(token);

      // 3. Verificar validez del token (firma y expiración)
      if (!jwtService.isTokenValid(token)) {
        // Si no es válido, salimos y permitimos que la cadena continúe SIN autenticación.
        // Spring Security lo tratará como 'no autenticado' más adelante.
        filterChain.doFilter(request, response);
        return;
      }

    } catch (ExpiredJwtException ex) {
      // Si el token está expirado, Spring Security lo manejará como no autenticado.
      logger.warn("Token JWT expirado: " + ex.getMessage());
      filterChain.doFilter(request, response);
      return;
    } catch (Exception ex) {
      // Otras excepciones de parseo o firma inválida
      logger.error("Error de token JWT: " + ex.getMessage());
      filterChain.doFilter(request, response);
      return;
    }

    // 4. Si el token es válido y no hay autenticación previa en el contexto
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      List<GrantedAuthority> authorities = new ArrayList<>();
      // Se recomienda verificar que 'role' no sea nulo/vacío
      if (role != null && !role.isEmpty()) {
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
      }

      // Creamos el objeto de autenticación
      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(username, null, authorities);

      // ** Establecemos la autenticación en el SecurityContext **
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    filterChain.doFilter(request, response);
  }
}

