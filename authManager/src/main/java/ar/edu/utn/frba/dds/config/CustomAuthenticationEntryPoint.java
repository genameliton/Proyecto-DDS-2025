package ar.edu.utn.frba.dds.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;
import org.hibernate.dialect.SybaseASEDialect;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // Por defecto 401 si no está autenticado
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    System.out.println(authException.getClass());
    String message = "";
    message = authException.getMessage();
    response.getWriter().write("{\"error\": \"" + message + "\"}");
  }
}