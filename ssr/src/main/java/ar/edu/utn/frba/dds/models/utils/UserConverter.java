package ar.edu.utn.frba.dds.models.utils;


import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {
  private final GoogleUserConverterStrategy googleUserAdapter;
  private final GithubUserConverterStrategy githubUserAdapter;

  public ExternalUser getUser(OAuth2User oauthUser, String requestUrl) {
    if (requestUrl.contains("google")) {
      return googleUserAdapter.convert(oauthUser);
    } else if (requestUrl.contains("github")) {
      return githubUserAdapter.convert(oauthUser);
    } else {
      throw new RuntimeException("Tipo de login no soportado");
    }
  }
}
