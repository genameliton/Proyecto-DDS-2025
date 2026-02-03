package ar.edu.utn.frba.dds.models.DTO;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class HechosPagDTO {
  private Integer current_page;
  private List<Hecho> data = new ArrayList<>();
  private Integer last_page;
  //private String first_page_url;
  //private Integer from;
//  @JsonProperty("last_page")
//  private Integer lastPage;
  //private String last_page_url;
  //private String next_page_url;
  //private String path;
  //private Integer per_page;
  //private String prev_page_url;
  //private Integer to;
  //private Integer total;
  public HechosPagDTO(Integer current_page, List<Hecho> data, Integer lastPage) {
    this.current_page = current_page;
    this.data = data;
    this.last_page = lastPage;
  }
}
