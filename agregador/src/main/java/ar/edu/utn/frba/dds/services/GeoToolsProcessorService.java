package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Lugar;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.index.strtree.STRtree; // IMPORTANTE
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import lombok.extern.slf4j.Slf4j;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.net.URL;

@Slf4j
@Service
public class GeoToolsProcessorService {
    private STRtree indexProvincias;
    private STRtree indexDepartamentos;

    private Set<String> nombresProvincias = new TreeSet<>();
    private Set<String> nombresDepartamentos = new TreeSet<>();

    private final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    private final ResourceLoader resourceLoader;

    @Value("${geotools.shapefile.provincias.path}")
    private String provinciasPath;

    @Value("${geotools.shapefile.departamentos.path}")
    private String departamentosPath;

    private static final String COLUMNA_NOMBRE = "NAME";

    public GeoToolsProcessorService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        this.indexProvincias = buildIndex(provinciasPath, "PROVINCIAS", nombresProvincias);
        this.indexDepartamentos = buildIndex(departamentosPath, "DEPARTAMENTOS", nombresDepartamentos);
    }

    private STRtree buildIndex(String path, String capa, Set<String> nombresSet) {
        STRtree index = new STRtree();
        try {
            URL url = resourceLoader.getResource(path).getURL();
            File file = new File(url.toURI());
            if (!file.exists())
                return null;

            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection collection = featureSource.getFeatures();

            try (SimpleFeatureIterator iterator = collection.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    org.locationtech.jts.geom.Geometry geom = (org.locationtech.jts.geom.Geometry) feature
                            .getDefaultGeometry();

                    if (geom != null) {
                        index.insert(geom.getEnvelopeInternal(), feature);

                        Object nombreObj = feature.getAttribute(COLUMNA_NOMBRE);
                        if (nombreObj != null)
                            nombresSet.add(nombreObj.toString().trim());
                    }
                }
            }
            index.build();
            log.info("GEOTOOLS: Índice espacial construido para {} con {} items.", capa, index.size());
            return index;
        } catch (Exception e) {
            log.error("GEOTOOLS: Error cargando {}", capa, e);
            return null;
        }
    }

    public Lugar buscarPorPoligono(double latitud, double longitud) {
        if (indexProvincias == null && indexDepartamentos == null)
            return null;

        Point punto = geometryFactory.createPoint(new Coordinate(longitud, latitud));
        Lugar lugar = new Lugar();

        if (indexDepartamentos != null) {
            SimpleFeature depto = buscarEnIndice(indexDepartamentos, punto);
            if (depto != null) {
                Object val = depto.getAttribute(COLUMNA_NOMBRE);
                if (val != null) {
                    lugar.setDepartamento(val.toString());
                    lugar.setMunicipio(val.toString());
                }
            }
        }

        if (indexProvincias != null) {
            SimpleFeature prov = buscarEnIndice(indexProvincias, punto);
            if (prov != null) {
                Object val = prov.getAttribute(COLUMNA_NOMBRE);
                if (val != null)
                    lugar.setProvincia(val.toString());
            }
        }

        if (lugar.getProvincia() != null)
            return lugar;
        return null;
    }

    private SimpleFeature buscarEnIndice(STRtree index, Point punto) {
        List<?> candidatos = index.query(punto.getEnvelopeInternal());

        for (Object obj : candidatos) {
            SimpleFeature feature = (SimpleFeature) obj;
            org.locationtech.jts.geom.Geometry geom = (org.locationtech.jts.geom.Geometry) feature.getDefaultGeometry();
            if (geom.contains(punto)) {
                return feature;
            }
        }
        return null;
    }

    public List<String> getNombresProvincias() {
        return new ArrayList<>(nombresProvincias);
    }

    public List<String> getNombresDepartamentos() {
        return new ArrayList<>(nombresDepartamentos);
    }
}