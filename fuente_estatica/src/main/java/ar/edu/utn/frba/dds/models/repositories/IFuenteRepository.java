package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFuenteRepository extends JpaRepository<Fuente, Long> {
}
