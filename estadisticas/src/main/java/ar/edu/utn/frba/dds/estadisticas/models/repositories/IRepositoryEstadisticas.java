package ar.edu.utn.frba.dds.estadisticas.models.repositories;

import ar.edu.utn.frba.dds.estadisticas.models.entities.Estadistica;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface IRepositoryEstadisticas extends JpaRepository<Estadistica, Long> {
  Optional<Estadistica> findByUrlColeccionAndCategoriaEspecifica(String urlColeccion, String categoriaEspecifica);

  List<Estadistica> findByVigente(int vigente);
}
