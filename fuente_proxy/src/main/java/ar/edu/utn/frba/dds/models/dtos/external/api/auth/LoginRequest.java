package ar.edu.utn.frba.dds.models.dtos.external.api.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
  private String email;
  private String password;

  public LoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
