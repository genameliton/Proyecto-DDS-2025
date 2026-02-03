package ar.edu.utn.frba.dds.models;

import java.util.List;

import ar.edu.utn.frba.dds.models.enums.Permiso;
import ar.edu.utn.frba.dds.models.enums.Rol;
import lombok.Data;

@Data
public class RolesPermisosDTO {
  private String username;
  private Rol rol;
  private List<Permiso> permisos;
}
