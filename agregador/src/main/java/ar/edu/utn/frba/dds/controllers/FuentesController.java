package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.services.ColeccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FuentesController {

    private final ColeccionService coleccionService;

    public FuentesController(ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
    }

    @PostMapping("/fuentes/refrescar-dinamica")
    public ResponseEntity<String> forzarRefresco() {
        coleccionService.refrescarFuenteDinamica();
        return ResponseEntity.ok("Refresco solicitado");
    }
}