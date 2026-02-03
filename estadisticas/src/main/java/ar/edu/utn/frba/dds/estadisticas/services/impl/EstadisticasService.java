package ar.edu.utn.frba.dds.estadisticas.services.impl;

import ar.edu.utn.frba.dds.estadisticas.models.dto.input.EstadisticaNuevaDTO;
import ar.edu.utn.frba.dds.estadisticas.models.entities.ConsultadorColeccion;
import ar.edu.utn.frba.dds.estadisticas.models.entities.DetalleEstadistica;
import ar.edu.utn.frba.dds.estadisticas.models.entities.Estadistica;
import ar.edu.utn.frba.dds.estadisticas.models.repositories.IRepositoryEstadisticas;
import ar.edu.utn.frba.dds.estadisticas.services.IEstadisticasService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EstadisticasService implements IEstadisticasService{
  private final IRepositoryEstadisticas repositoryEstadisticas;
  private final ConsultadorColeccion consultadorColeccion;
  public EstadisticasService(IRepositoryEstadisticas repositoryEstadisticas, ConsultadorColeccion consultadorColeccion) {
    this.repositoryEstadisticas = repositoryEstadisticas;
    this.consultadorColeccion = consultadorColeccion;
  }

  @Override
  public Estadistica createEstadistica(EstadisticaNuevaDTO dto) {
    if (dto.getUrlColeccion() == null || dto.getUrlColeccion().isBlank() || dto.getCategoriaEspecifica() == null || dto.getCategoriaEspecifica().isBlank()) {
      throw new IllegalArgumentException("Url de coleccion y categoria especifica no pueden ser vacias o nulas");
    }

    Optional<Estadistica> estadisticaExistente = repositoryEstadisticas.findByUrlColeccionAndCategoriaEspecifica(dto.getUrlColeccion(), dto.getCategoriaEspecifica());
    if (estadisticaExistente.isPresent()){
      throw new EntityExistsException("Ya existe una estadistica con los datos ingresados");
    }
    Estadistica estadistica = consultadorColeccion.generarEstadistica(dto.getUrlColeccion(), dto.getCategoriaEspecifica());
    Estadistica estadisticaGuardada = repositoryEstadisticas.save(estadistica);
    log.info("Estadistica creada sobre coleccion {}, url:{}", estadisticaGuardada.getNombre(), estadisticaGuardada.getUrlColeccion());
    return estadisticaGuardada;
  }

  @Override
  @Transactional
  public void actualizarEstadisticas(){
    List<Estadistica> estadisticas = repositoryEstadisticas.findByVigente(1);
    estadisticas.forEach(estadistica -> {
      try {
        DetalleEstadistica detallesNuevos = consultadorColeccion.calcularDetalles(estadistica);
        estadistica.setDetalle(detallesNuevos);
      } catch (Exception e){
        //coleccion
        estadistica.setVigente(0);
      }
    });
    repositoryEstadisticas.saveAll(estadisticas);
  }

  @Override
  @Transactional
  public void eliminarEstadistica(Long id) {
    Estadistica estadistica = this.getEstadisticaById(id);
    repositoryEstadisticas.deleteById(id);
  }

  @Override
  public List<Estadistica> getEstadisticas() {
    return repositoryEstadisticas.findAll();
  }

  @Override
  public Estadistica getEstadisticaById(Long estadisticaId) {
    return repositoryEstadisticas.findById(estadisticaId).orElseThrow(
        () ->  new EntityNotFoundException("Estadistica con id " + estadisticaId + " no encontrada")
    );
  }

  @Override
  public String exportarEstadisticasCSV(String rutaArchivo) {
    List<Estadistica> estadisticas = repositoryEstadisticas.findAll();

    try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
      // Cabecera
      writer.println("Id_estadistica; titulo_coleccion; url ; categoria_especifica ; categoria_mayor_cantidad_hechos ; provincia_mayor_cantidad_hechos ; provincia_mayor_cantidad_hechos_categoria_especific ; hora_con_mayor_cantidad_de_hechos ; cantidad_solicitudes_spam");

      // Datos
      for (Estadistica e : estadisticas) {
          writer.printf("%d;%s;%s;%s;%s;%s;%s;%s;%s;%n",
              e.getId(),
              e.getNombre(),
              e.getUrlColeccion(),
              e.getCategoriaEspecifica(),
              e.getDetalle().getCategoriaMayoresHechos(),
              e.getDetalle().getProvinciaMayorCantHechos(),
              e.getDetalle().getProvinciaMayorCantHechosCategoria(),
              e.getDetalle().getHoraMayorCantHechos(),
              e.getDetalle().getSolicitudesSpam()
          );
        }
    } catch (IOException ex) {
      throw new RuntimeException("Error exportando estadísticas a CSV", ex);
    }
    log.info("Estadisticas exportadas a CSV");
    return rutaArchivo;
  }

  @Override
  @Transactional
  public void eliminarEstadisticasNoVigentes() {
    List<Estadistica> estadisticas = repositoryEstadisticas.findByVigente(1);
    repositoryEstadisticas.deleteAll(estadisticas);
  }
}
