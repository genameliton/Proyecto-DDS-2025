package ar.edu.utn.frba.dds.models.repositories.specs;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HechoSpecs {
    public static Specification<Hecho> deFuentes(List<String> fuenteIds) {
        return (root, query, cb) -> {
            if (fuenteIds == null || fuenteIds.isEmpty()) {
                return cb.disjunction();
            }
            query.distinct(true);
            Join<Hecho, Fuente> fuentesJoin = root.join("fuentes", JoinType.INNER);
            return fuentesJoin.get("id").in(fuenteIds);
        };
    }

    public static Specification<Hecho> deConsenso(Long algoritmoId) {
        return (root, query, cb) -> {
            if (algoritmoId == null)
                return cb.disjunction();

            query.distinct(true);
            Join<Hecho, IConsensoStrategy> consensoJoin = root.join("consensos", JoinType.INNER);
            return cb.equal(consensoJoin.get("id"), algoritmoId);
        };
    }

    public static Specification<Hecho> excluirEliminados() {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Solicitud> subRoot = subquery.from(Solicitud.class);

            subquery.select(subRoot.get("hecho").get("id"));
            subquery.where(cb.equal(subRoot.get("estadoActual").get("estado"), TipoEstado.ACEPTADA));

            // WHERE hecho.id NOT IN (subquery)
            return cb.not(root.get("id").in(subquery));
        };
    }

    public static Specification<Hecho> conTituloLike(String texto) {
        return (root, query, cb) -> {
            if (texto == null || texto.isBlank())
                return null;
            return cb.like(cb.lower(root.get("titulo")), "%" + texto.toLowerCase() + "%");
        };
    }

    public static Specification<Hecho> conCategoria(String categoria) {
        return (root, query, cb) -> {
            if (categoria == null || categoria.isBlank())
                return null;
            return cb.like(cb.lower(root.get("categoria")), "%" + categoria.toLowerCase() + "%");
        };
    }

    public static Specification<Hecho> fechaAcontecimientoEntre(LocalDate inicio, LocalDate fin) {
        return (root, query, cb) -> {
            if (inicio == null && fin == null)
                return null;

            List<Predicate> predicates = new ArrayList<>();

            if (inicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaAcontecimiento"), inicio.atStartOfDay()));
            }
            if (fin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaAcontecimiento"), fin.atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Hecho> fechaReporteEntre(LocalDate inicio, LocalDate fin) {
        return (root, query, cb) -> {
            if (inicio == null && fin == null)
                return null;

            List<Predicate> predicates = new ArrayList<>();
            if (inicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaCarga"), inicio.atStartOfDay()));
            }
            if (fin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaCarga"), fin.atTime(23, 59, 59)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Hecho> enProvincia(String provincia) {
        return (root, query, cb) -> {
            if (provincia == null || provincia.isBlank())
                return null;
            // Navegación: Hecho -> Ubicacion (Embedded) -> Lugar (Embedded) -> provincia
            return cb.like(
                    cb.lower(root.get("ubicacion").get("lugar").get("provincia")),
                    "%" + provincia.toLowerCase() + "%");
        };
    }

    public static Specification<Hecho> enMunicipio(String municipio) {
        return (root, query, cb) -> {
            if (municipio == null || municipio.isBlank())
                return null;
            return cb.like(
                    cb.lower(root.get("ubicacion").get("lugar").get("municipio")),
                    "%" + municipio.toLowerCase() + "%");
        };
    }

    public static Specification<Hecho> enDepartamento(String departamento) {
        return (root, query, cb) -> {
            if (departamento == null || departamento.isBlank())
                return null;
            return cb.like(
                    cb.lower(root.get("ubicacion").get("lugar").get("departamento")),
                    "%" + departamento.toLowerCase() + "%");
        };
    }

    public static Specification<Hecho> conTipoFuente(String tipoFuenteStr) {
        return (root, query, cb) -> {
            if (tipoFuenteStr == null || tipoFuenteStr.isBlank())
                return null;
            try {
                TipoFuente tipo = TipoFuente.valueOf(tipoFuenteStr.toUpperCase());
                return cb.equal(root.get("origen").get("tipo"), tipo);
            } catch (IllegalArgumentException e) {
                return null; // O lanzar excepción si prefieres ser estricto
            }
        };
    }

    public static Specification<Hecho> busquedaGeneral(String queryStr) {
        return (root, query, cb) -> {
            if (queryStr == null || queryStr.isBlank())
                return null;
            String pattern = "%" + queryStr.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("titulo")), pattern),
                    cb.like(cb.lower(root.get("descripcion")), pattern),
                    cb.like(cb.lower(root.get("categoria")), pattern));
        };
    }
}