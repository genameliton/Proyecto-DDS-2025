package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.DTO.output.FuenteCsvDTOOutput;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface IFuenteEstaticaService {

  FuenteCsvDTOOutput getFuente(Long id);

  List<Hecho> getHechos(Long id);

  void eliminarFuente(Long id);

  List<FuenteCsvDTOOutput> obtenerFuentesDTO();

  List<Fuente> getFuentes();

  Map<String, Object> validarCsv(MultipartFile file);

  FuenteCsvDTOOutput crearNuevaFuente(MultipartFile link) throws IOException;
}
