package ar.edu.utn.frba.dds.models.dtos.output;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PaginacionDTOSalida<T> {
  private List<T> data;
  private int currentPage;
  private int totalPages;
}
