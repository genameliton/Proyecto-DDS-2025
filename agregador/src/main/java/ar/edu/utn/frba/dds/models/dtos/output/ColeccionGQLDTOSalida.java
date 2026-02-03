package ar.edu.utn.frba.dds.models.dtos.output;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import java.util.List;
import lombok.Data;

@Data
public class ColeccionGQLDTOSalida {
  private String id;
  private String titulo;
  private String descripcion;
  private List<FuenteDTOSalida> fuentes;
  private Integer cantSolicitudesSpam;
  private List<FiltroDTOSalida> criterios;
  private String algoritmoConsenso;
  private PaginacionDTOSalida<HechoDTOSalida> hechos;
  private String estado;

  public ColeccionGQLDTOSalida(ColeccionDTOSalida dto) {
    this.id = dto.getId();
    this.titulo = dto.getTitulo();
    this.descripcion = dto.getDescripcion();
    this.fuentes = dto.getFuentes();
    this.cantSolicitudesSpam = dto.getCantSolicitudesSpam();
    this.criterios = dto.getCriterios();
    this.algoritmoConsenso = dto.getAlgoritmoConsenso();
    this.estado = dto.getEstado().toString();
  }
}
