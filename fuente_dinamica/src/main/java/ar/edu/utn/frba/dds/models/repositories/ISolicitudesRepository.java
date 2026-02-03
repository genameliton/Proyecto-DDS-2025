package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISolicitudesRepository extends JpaRepository<Solicitud,Long> {
    List<Solicitud> findByEstadoSolicitud(EstadoSolicitud estadoSolicitud);

    @Query("SELECT s FROM Solicitud s WHERE s.estadoSolicitud = 'PENDIENTE'")
    List<Solicitud> findSolicitudesPendientes();

    List<Solicitud> findByHecho(Hecho hecho);
}
