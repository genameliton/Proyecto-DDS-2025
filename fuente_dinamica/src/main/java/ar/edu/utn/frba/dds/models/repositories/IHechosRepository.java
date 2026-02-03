package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.enums.EstadoHecho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {

  List<Hecho> findByEstadoHechoIn(List<EstadoHecho> estados);

  List<Hecho> findByEstadoHecho(EstadoHecho estadoHecho);

  @Query(value = "SELECT * FROM hechos WHERE estado = 'PENDIENTE'", nativeQuery = true)
  List<Hecho> findHechosPendientes();

  @Query(value = "SELECT * FROM hechos WHERE estado = 'RECHAZADO'", nativeQuery = true)
  List<Hecho> findHechosRechazados();

  @Query(value = "SELECT * FROM hechos WHERE estado = 'ACEPTADO'", nativeQuery = true)
  List<Hecho> findHechosAceptados();

  @Query(value = "SELECT * FROM hechos WHERE estado IN ('PENDIENTE', 'ACEPTADO_CON_SUGERENCIAS') AND nombre_autor = ?1", nativeQuery = true)
  List<Hecho> findHechosPendientesByCreator(String username);

  @Query(value = "SELECT * FROM hechos WHERE nombre_autor = ?1 ORDER BY fecha_carga DESC", nativeQuery = true)
  List<Hecho> findHechosByCreator(String username);
}
