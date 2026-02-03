package ar.edu.utn.frba.dds.models.dtos.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class HechoPagDTO {
  @JsonProperty("current page")
  private Integer currentPage;
  private List<HechoOutputDTO> data;
  //private String first_page_url;
  //private Integer from;
  @JsonProperty("last_page")
  private Integer lastPage;
  //private String last_page_url;
  //private String next_page_url;
  //private String path;
  //private Integer per_page;
  //private String prev_page_url;
  //private Integer to;
  //private Integer total;

  public HechoPagDTO(Integer currentPage, List<HechoOutputDTO> data) {
    this.currentPage = currentPage;
    this.data = data;
  }
}
