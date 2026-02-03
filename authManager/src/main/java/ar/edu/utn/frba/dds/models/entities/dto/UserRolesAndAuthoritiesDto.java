package ar.edu.utn.frba.dds.models.entities.dto;

import ar.edu.utn.frba.dds.models.entities.Permiso;
import ar.edu.utn.frba.dds.models.entities.TipoRol;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRolesAndAuthoritiesDto {
  private String username;
  private TipoRol rol;
  private List<Permiso> permisos;
}
