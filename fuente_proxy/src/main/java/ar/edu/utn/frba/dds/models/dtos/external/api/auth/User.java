package ar.edu.utn.frba.dds.models.dtos.external.api.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
  private Integer id;
  private String name;
  private String email;
  private String email_verified_at;
  private String created_at;
  private String updated_at;
}
