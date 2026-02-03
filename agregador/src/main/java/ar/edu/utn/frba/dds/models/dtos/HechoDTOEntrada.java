package ar.edu.utn.frba.dds.models.dtos;
//{
//    "id": 10,
//    "titulo": "Evacuaciones por Fenómeno meteorológico con granizo en Salta, Salta",
//    "descripcion": "Severa fenómeno meteorológico con granizo impactó en la localidad de Salta, Salta. El incidente dejando a varios sectores sin comunicación. Las autoridades locales han desplegado equipos de emergencia para atender a los afectados.",
//    "categoria": "Fenómeno meteorológico con granizo",
//    "latitud": -24.77728,
//    "longitud": -65.402076,
//    "fecha_hecho": "2024-03-03T00:00:00.000000Z",
//    "created_at": "2025-05-06T22:14:14.000000Z",
//    "updated_at": "2025-05-06T22:14:14.000000Z"
//    }

import ar.edu.utn.frba.dds.models.dtos.input.MultimediaDTOEntrada;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class HechoDTOEntrada {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  @JsonProperty("fecha_hecho")
  private LocalDateTime fechaHecho;
  @JsonProperty("created_at")
  private LocalDateTime createdAt;
  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;
  private List<MultimediaDTOEntrada> multimedia;
  private String autor;
}
