package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IFuenteRepository extends JpaRepository<Fuente, String> {
  Optional<Fuente> findByUrlAndTipoFuente(String url, TipoFuente tipoFuente);

  Optional<Fuente> findByTipoFuente(TipoFuente tipoFuente);

  @Query("SELECT f FROM Fuente f JOIN f.hechos h WHERE h.id = :hechoId")
  List<Fuente> findFuentesByHechoId(@Param("hechoId") Long hechoId);
}
