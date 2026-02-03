package ar.edu.utn.frba.dds.models.utils;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class GithubUserConverterStrategy implements UserConverterStrategy {
  @Override
  public ExternalUser convert(OAuth2User user) {
    String username = user.getAttribute("name");
    return new ExternalUser(username, null, "github");
  }
}
