package ar.edu.utn.frba.dds.models;

import java.util.List;
import lombok.Data;

@Data
public class HechoPaginadoDTOSalida {
  private Integer currentPage;
  private Integer totalPages;
  private List<HechoPaginacionDTO> data;
}
