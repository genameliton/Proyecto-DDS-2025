package ar.edu.utn.frba.dds.models.dtos.external.api.hecho;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class HechosPagDTO {
  @JsonProperty("current_page")
  private Integer currentPage;
  private List<HechoDTO> data;
  @JsonProperty("last_page")
  private Integer lastPage;
}
