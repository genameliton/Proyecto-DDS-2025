package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.DTO.output.FuenteCsvDTOOutput;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.services.IFuenteEstaticaService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/fuentes")
@CrossOrigin(origins = "*")
public class FuentesController {
  private IFuenteEstaticaService fuenteEstaticaService;

  public FuentesController(IFuenteEstaticaService fuenteEstaticaService) {
    this.fuenteEstaticaService = fuenteEstaticaService;
  }

  @GetMapping("/{id}/hechos")
  public List<Hecho> getHechos(@PathVariable(required = true) Long id) {
    return fuenteEstaticaService.getHechos(id);
  }

  @PostMapping("")
  public FuenteCsvDTOOutput crearFuenteCsv(@RequestParam("file") MultipartFile file) throws IOException {
    return fuenteEstaticaService.crearNuevaFuente(file);
  }

  @PostMapping("/validar-csv")
  public ResponseEntity<Map<String, Object>> validarCsv(@RequestParam("file") MultipartFile file) {
    Map<String, Object> response = fuenteEstaticaService.validarCsv(file);
    if (response.get("error") == null) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> eliminarFuente(@PathVariable Long id) {
    fuenteEstaticaService.eliminarFuente(id);
    return ResponseEntity.ok("Operacion completada");
  }

  @GetMapping("/")
  public List<FuenteCsvDTOOutput> obtenerFuentes() {
    return fuenteEstaticaService.obtenerFuentesDTO();
  }
}
