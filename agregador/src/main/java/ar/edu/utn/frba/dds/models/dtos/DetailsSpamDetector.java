package ar.edu.utn.frba.dds.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class DetailsSpamDetector {

  @JsonProperty("isContentSpam")
  private String contentSpamStatus;

  @JsonProperty("numberOfSpamWords")
  private int spamWordsNum;

  @JsonProperty("spamWords")
  private List<String> spamWords;

  public DetailsSpamDetector(String contentSpamStatus, int spamWordsNum, List<String> spamWords) {
    this.contentSpamStatus = contentSpamStatus;
    this.spamWordsNum = spamWordsNum;
    this.spamWords = spamWords;
  }
}