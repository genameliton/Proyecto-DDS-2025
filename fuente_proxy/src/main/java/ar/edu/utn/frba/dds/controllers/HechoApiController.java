package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.external.api.hecho.HechoDTO;
import ar.edu.utn.frba.dds.services.HechoApiService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/hechos")
public class HechoApiController {
  private final HechoApiService hechoApiService;

  public HechoApiController(HechoApiService hechoApiService) {
    this.hechoApiService = hechoApiService;
  }

  @GetMapping("/{id}")
  public Mono<HechoDTO> getHechoById(@PathVariable Integer id) {
    return hechoApiService.getHechoById(id);
  }

  @GetMapping
  public Flux<HechoDTO> getHechos() {
    return hechoApiService.getHechos();
  }
}