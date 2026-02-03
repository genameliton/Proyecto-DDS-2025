package ar.edu.utn.frba.dds.models.repositories;

import java.util.List;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.enums.EstadoColeccion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IColeccionRepository extends JpaRepository<Coleccion, String> {
    @Query("SELECT c FROM Coleccion c JOIN c.fuentes f WHERE f.url LIKE %:sufijoUrl% AND c.estado = 'PROCESANDO'")
    List<Coleccion> findColeccionesProcesandoPorUrlFuente(@Param("sufijoUrl") String sufijoUrl);

    List<Coleccion> findByEstado(EstadoColeccion estado);
}
