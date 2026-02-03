package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ISolicitudEliminacionRepository extends JpaRepository<Solicitud, Long> {
  @Query("SELECT s FROM Solicitud s WHERE s.estadoActual.estado = :tipoEstado")
  Page<Solicitud> findByEstadoActual(TipoEstado tipoEstado, Pageable pageable);

  Page<Solicitud> findByCreador(String username, Pageable pageable);

  @Query("SELECT s FROM Solicitud s WHERE s.creador = :username AND s.estadoActual.estado = :tipoEstado")
  Page<Solicitud> findByCreadorAndEstadoActual(@Param("username") String username,
      @Param("tipoEstado") TipoEstado tipoEstado,
      Pageable pageable);

  long countByEstadoActual_Estado(TipoEstado estado);
}
