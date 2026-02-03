package ar.edu.utn.frba.dds.models.dtos.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevisionInputDTO {
  private String supervisor;
  private String comentario; //Motivo de rechazo o sugerencias
}
