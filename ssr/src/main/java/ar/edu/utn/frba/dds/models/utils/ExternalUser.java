package ar.edu.utn.frba.dds.models.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExternalUser {
  private String username;
  private String password;
  private String provider;
}