package ar.edu.utn.frba.dds.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpamDetectorRequestDTO {
  @JsonProperty
  private String content;

  public SpamDetectorRequestDTO(String content) {
    this.content = content;
  }
}
