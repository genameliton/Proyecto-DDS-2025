package ar.edu.utn.frba.dds.models.entities.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class NewUserDto {
  private String username;
  private String password;
}
