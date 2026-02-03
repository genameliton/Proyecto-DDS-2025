package ar.edu.utn.frba.dds.models.utils;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserConverterStrategy {
  public ExternalUser convert(OAuth2User user);
}

