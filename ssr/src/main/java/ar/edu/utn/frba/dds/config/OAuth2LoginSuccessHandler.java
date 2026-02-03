package ar.edu.utn.frba.dds.config;

import ar.edu.utn.frba.dds.ExternalApiException;
import ar.edu.utn.frba.dds.models.AuthResponseDTO;
import ar.edu.utn.frba.dds.models.utils.ExternalUser;
import ar.edu.utn.frba.dds.models.utils.UserConverter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final AuthProvider authProvider;
  private final UserConverter userConverter;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
    String url = request.getRequestURL().toString();

    ExternalUser externalUser = userConverter.getUser(oauthUser, url);
    String username = externalUser.getUsername();
    String provider = externalUser.getProvider();
    try {
      AuthResponseDTO tokens = authProvider.loginOAuth(username, provider);

      request.getSession().setAttribute("accessToken", tokens.getAccessToken());
      request.getSession().setAttribute("refreshToken", tokens.getRefreshToken());
      request.getSession().setAttribute("username", username);

      var rolesPermisos = authProvider.getRolesPermisos(tokens.getAccessToken());
      request.getSession().setAttribute("rol", rolesPermisos.getRol());
      request.getSession().setAttribute("permisos", rolesPermisos.getPermisos());

      List<GrantedAuthority> authorities = new ArrayList<>();
      rolesPermisos.getPermisos().forEach(permiso -> {
        authorities.add(new SimpleGrantedAuthority(permiso.name()));
      });
      authorities.add(new SimpleGrantedAuthority("ROLE_" + rolesPermisos.getRol().name()));
      response.sendRedirect("/");
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 409) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        response.sendRedirect("/registro?userExists");
      }
    } catch (ExternalApiException e) {
      response.sendRedirect("/login?authErr");
      SecurityContextHolder.clearContext();
      request.getSession().invalidate();
    }

    catch (Exception e) {
      response.sendRedirect("/login?auth0err");
      log.error("Error en OAuth2LoginSuccessHandler: ", e);
      SecurityContextHolder.clearContext();
      request.getSession().invalidate();
    }
  }
}