package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrigenRepository extends JpaRepository<Origen, Long> {
  Optional<Origen> findFirstByTipoAndAutor(TipoFuente tipo, String autor);
}
