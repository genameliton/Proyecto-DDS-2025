package ar.edu.utn.frba.dds.models.dtos.external.api.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginData {
  private String access_token;
  private String token_type;
  private User user;
}