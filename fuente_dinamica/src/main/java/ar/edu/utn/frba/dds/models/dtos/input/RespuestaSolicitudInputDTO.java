package ar.edu.utn.frba.dds.models.dtos.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaSolicitudInputDTO {
    private String supervisor; //email
    private String comentario; //opcional
}
