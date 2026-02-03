package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Multimedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMultimediaJpaRepository extends JpaRepository<Multimedia, Long> {
}
