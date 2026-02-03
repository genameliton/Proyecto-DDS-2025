package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.DTO.output.FuenteCsvDTOOutput;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.utils.ExtractorHechosCSV;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import ar.edu.utn.frba.dds.services.IFuenteEstaticaService;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FuenteEstaticaService implements IFuenteEstaticaService {
  private IFuenteRepository fuenteRepository;
  private ExtractorHechosCSV extractorHechosCSV;

  public FuenteEstaticaService(IFuenteRepository fuenteRepository, ExtractorHechosCSV extractorHechosCSV) {
    this.fuenteRepository = fuenteRepository;
    this.extractorHechosCSV = extractorHechosCSV;
  }

  @Override
  public FuenteCsvDTOOutput getFuente(Long id) {
    Fuente fuente = fuenteRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Fuente no encontrada"));
    return new FuenteCsvDTOOutput(fuente.getId(), fuente.getUrl(), fuente.getHechos().size());
  }

  @Override
  public List<Hecho> getHechos(Long id) {
    Fuente fuente = fuenteRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Fuente no encontrada"));
    return fuente.getHechos();
  }

  @Override
  public void eliminarFuente(Long id) {
    fuenteRepository.deleteById(id);
  }

  @Override
  public List<FuenteCsvDTOOutput> obtenerFuentesDTO() {
    List<Fuente> fuentes = this.getFuentes();
    return fuentes.stream().map(f -> new FuenteCsvDTOOutput(f.getId(), f.getUrl(), f.getHechos().size())).toList();
  }

  @Override
  public List<Fuente> getFuentes() {
    return fuenteRepository.findAll();
  }

  @Override
  public Map<String, Object> validarCsv(MultipartFile file) {
    return extractorHechosCSV.obtenerDatosValidacion(file);
  }

  @Override
  public FuenteCsvDTOOutput crearNuevaFuente(MultipartFile link) throws IOException {
    Fuente fuente = new Fuente();
    List<Hecho> hechosCsv = this.extractorHechosCSV.obtenerHechos(link);
    fuente.setHechos(hechosCsv);
    fuente.setUrl(link.getOriginalFilename());
    Fuente fuenteGuardado = fuenteRepository.save(fuente);
    log.info("Nueva fuente csv de archivo {}", link.getOriginalFilename());
    return new FuenteCsvDTOOutput(fuente.getId(), link.getOriginalFilename(), fuenteGuardado.getHechos().size());
  }
}
