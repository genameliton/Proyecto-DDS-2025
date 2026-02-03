package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.HechoUpdateDTO;
import ar.edu.utn.frba.dds.models.dtos.input.RevisionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoRevisionOutputDTO;
import ar.edu.utn.frba.dds.services.HechosService;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/hechos")
public class HechosController {
  private final HechosService hechosService;

  public HechosController(HechosService hechosService) {
    this.hechosService = hechosService;
  }

  @GetMapping
  public List<HechoOutputDTO> getHechos() {
    return hechosService.getHechos();
  }

  @GetMapping("/{id}")
  public ResponseEntity<HechoOutputDTO> getHechoById(@PathVariable Long id) {
    return ResponseEntity.ok(hechosService.getHechoById(id));
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public HechoOutputDTO crearHecho(
      @RequestPart("hecho") HechoInputDTO hechoDto,
      @RequestPart(value = "multimedia", required = false) List<MultipartFile> multimedia) {
    return hechosService.crearHecho(hechoDto, multimedia);
  }

  @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTRIBUYENTE')")
  @PutMapping("/{id}")
  public ResponseEntity<?> modificarHecho(@PathVariable Long id,
      @RequestPart("hecho") HechoUpdateDTO hechoDto,
      @RequestPart(value = "multimedia", required = false) List<MultipartFile> multimedia) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      String username = authentication.getName();

      HechoOutputDTO hechoActualizado = hechosService.actualizarHecho(id, hechoDto, multimedia, username);

      return ResponseEntity.ok(hechoActualizado);
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    } catch (RuntimeException ex) {
      System.out.println(ex.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/pendientes_por_creador")
  public List<HechoRevisionOutputDTO> getHechosPendientesBycreador() {
    return hechosService.getHechosPendientesByCreador();
  }

  // ADMINISTRADOR

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/pendientes")
  public List<HechoRevisionOutputDTO> getHechosPendientes(
  // @RequestParam(required = false, defaultValue = "1") int page,
  // @RequestParam(required = false, defaultValue = "15") int perPage
  ) {
    return hechosService.getHechosPendientes();
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PutMapping("/{id}/aceptar")
  public ResponseEntity<HechoRevisionOutputDTO> aceptarHecho(@PathVariable Long id,
      @RequestBody RevisionInputDTO revisionDto) {
    try {
      HechoRevisionOutputDTO hechoAceptado = hechosService.aceptarHecho(id, revisionDto);
      return ResponseEntity.ok(hechoAceptado);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PutMapping("/{id}/aceptar-con-sugerencias")
  public ResponseEntity<HechoRevisionOutputDTO> aceptarHechoConSugerencias(@PathVariable Long id,
      @RequestBody RevisionInputDTO revisionDto) {
    try {
      HechoRevisionOutputDTO hechoAceptado = hechosService.aceptarHechoConSugerencias(id, revisionDto);
      return ResponseEntity.ok(hechoAceptado);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PutMapping("/{id}/rechazar")
  public ResponseEntity<HechoRevisionOutputDTO> rechazarHecho(
      @PathVariable Long id,
      @RequestBody RevisionInputDTO revisionDto) {
    try {
      HechoRevisionOutputDTO hechoRechazado = hechosService.rechazarHecho(id, revisionDto);
      return ResponseEntity.ok(hechoRechazado);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
