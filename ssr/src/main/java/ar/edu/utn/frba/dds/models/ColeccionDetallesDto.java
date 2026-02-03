package ar.edu.utn.frba.dds.models;

import java.util.List;
import lombok.Data;

@Data
public class ColeccionDetallesDTO {
  private List<HechoDTO> data;
  private Integer currentPage;
  private Integer perPage;
  private Integer totalPages;
}
