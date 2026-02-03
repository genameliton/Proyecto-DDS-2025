package ar.edu.utn.frba.dds.mappers;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.MultimediaOutputDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;

public class HechoMapper {

        public static Hecho toEntity(HechoInputDTO hechoInputDTO) {
                return Hecho.builder()
                                .titulo(hechoInputDTO.getTitulo())
                                .descripcion(hechoInputDTO.getDescripcion())
                                .categoria(hechoInputDTO.getCategoria())
                                .ubicacion(Ubicacion.builder()
                                                .latitud(hechoInputDTO.getLatitud())
                                                .longitud(hechoInputDTO.getLongitud())
                                                .build())
                                .fechaAcontecimiento(hechoInputDTO.getFechaAcontecimiento())
                                .nombreAutor(hechoInputDTO.getAutor())
                                .build();
        }

        public static HechoOutputDTO toHechoOutputDTO(Hecho hecho) {
                return HechoOutputDTO.builder()
                                .id(hecho.getId())
                                .titulo(hecho.getTitulo())
                                .descripcion(hecho.getDescripcion())
                                .categoria(hecho.getCategoria())
                                .latitud(hecho.getUbicacion().getLatitud())
                                .longitud(hecho.getUbicacion().getLongitud())
                                .fechaHecho(hecho.getFechaAcontecimiento())
                                .createdAt(hecho.getFechaCarga())
                                .updatedAt(hecho.getFechaUltimaModificacion())
                                .multimedia(hecho.getMultimedia().stream().map(m -> MultimediaOutputDTO.builder()
                                                .nombre(m.getNombre())
                                                .ruta(m.getRuta())
                                                .formato(m.getFormato().name())
                                                .build())
                                                .toList())
                                .autor(hecho.getNombreAutor())
                                .build();
        }
}