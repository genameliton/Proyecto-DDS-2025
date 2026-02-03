package ar.edu.utn.frba.dds.models.DTO.output;

import lombok.Getter;

@Getter
public class FuenteCsvDTOOutput {
  private String link;
  private int cantidadHechos;
  private Long id;
  public FuenteCsvDTOOutput(Long id, String link, int cantidadHechos) {
    this.link = link;
    this.cantidadHechos = cantidadHechos;
    this.id = id;
  }
}
