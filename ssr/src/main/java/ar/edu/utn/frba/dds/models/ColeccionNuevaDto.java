package ar.edu.utn.frba.dds.models;

import java.util.List;
import lombok.Data;

@Data
public class ColeccionNuevaDTO {
  private String titulo;
  private String descripcion;
  private List<FuenteNuevaDTO> fuentes;
  private String algoritmoConsenso;
  private List<FiltroDTOSalida> criterios;
}
