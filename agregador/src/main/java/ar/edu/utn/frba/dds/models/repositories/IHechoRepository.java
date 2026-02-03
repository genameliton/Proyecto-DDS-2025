package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IHechoRepository extends JpaRepository<Hecho, Long>, JpaSpecificationExecutor<Hecho> {
        // List<Hecho> busquedaTexto(String textoTitulo);
        // logica para normalizar categoria
        @Query(value = "SELECT categoria " +
                        "FROM hecho " +
                        "WHERE MATCH(categoria, titulo) AGAINST (:categoria) >= 5 " +
                        "ORDER BY MATCH(categoria, titulo) AGAINST (:categoria) DESC LIMIT 1", nativeQuery = true)
        Optional<String> buscarCategoriaNormalizada(@Param("categoria") String categoria);

        Optional<Hecho> findByTituloAndDescripcionAndFechaAcontecimiento(String titulo, String descripcion,
                        LocalDateTime fechaAcontecimiento);

        Hecho findFirstByUbicacion_LatitudAndUbicacion_LongitudAndUbicacion_Lugar_ProvinciaIsNotNull(
                        Double latitud, Double longitud);

        @Query("SELECT h FROM Hecho h JOIN h.origen o WHERE o.autor = :autor AND h.fechaAcontecimiento = :fecha")
        List<Hecho> findPosiblesDuplicados(@Param("autor") String autor, @Param("fecha") LocalDateTime fecha);
}
