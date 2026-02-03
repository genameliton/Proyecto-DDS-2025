package ar.edu.utn.frba.dds.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SpamDetectorResponseDTO {

  @JsonProperty("Score")
  private int score;

  @JsonProperty("Details")
  private DetailsSpamDetector details;

  public SpamDetectorResponseDTO(DetailsSpamDetector details, int score) {
    this.details = details;
    this.score = score;
  }

}