/*package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.models.entities.adapters.CsvReaderAdapter;
import ar.edu.utn.frba.dds.models.entities.enums.Estado;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroCategoriaStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroEtiquetaStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroFechaStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroTituloStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PrimeraEntregaTest {
    private Set<Hecho> hechos;
    private FuenteDeDatos fuente;
    private Coleccion coleccion;

    @BeforeEach
    public void setUp() {
        fuente = Mockito.mock(FuenteEstaticaCsv.class);
        hechos = new HashSet<>();
        hechos.add(Hecho.builder()
                .titulo("Caída de aeronave impacta en Olavarría")
                .descripcion("Grave caída de aeronave ocurrió en las inmediaciones de Olavarría, Buenos Aires. El incidente provocó pánico entre los residentes locales. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.")
                .categoria(new Categoria("Caída de aeronave"))
                .ubicacion(new Ubicacion(-36.868375, -60.343297))
                .fechaAcontecimiento(LocalDateTime.of(2001, 11, 29, 0, 0))
                .build());
        hechos.add(Hecho.builder()
                .titulo("Serio incidente: Accidente con maquinaria industrial en Chos Malal, Neuquén")
                .descripcion("Un grave accidente con maquinaria industrial se registró en Chos Malal, Neuquén. El incidente dejó a varios sectores sin comunicación. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.")
                .categoria(new Categoria("Accidente con maquinaria industrial"))
                .ubicacion(new Ubicacion(-37.345571, -70.241485))
                .fechaAcontecimiento(LocalDateTime.of(2001, 8, 16, 0, 0))
                .build());
        hechos.add(Hecho.builder()
                .titulo("Caída de aeronave impacta en Venado Tuerto, Santa Fe")
                .descripcion("Grave caída de aeronave ocurrió en las inmediaciones de Venado Tuerto, Santa Fe. El incidente destruyó viviendas y dejó a familias evacuadas. Autoridades nacionales se han puesto a disposición para brindar asistencia.")
                .categoria(new Categoria("Caída de aeronave"))
                .ubicacion(new Ubicacion(-33.768051, -61.921032))
                .fechaAcontecimiento(LocalDateTime.of(2008, 8, 8, 0, 0))
                .build());
        hechos.add(Hecho.builder()
                .titulo("Accidente en paso a nivel deja múltiples daños en Pehuajó, Buenos Aires")
                .descripcion("Grave accidente en paso a nivel ocurrió en las inmediaciones de Pehuajó, Buenos Aires. El incidente generó preocupación entre las autoridades provinciales. El Ministerio de Desarrollo Social está brindando apoyo a los damnificados.")
                .categoria(new Categoria("Accidente en paso a nivel"))
                .ubicacion(new Ubicacion(-35.855811, -61.940589))
                .fechaAcontecimiento(LocalDateTime.of(2020, 1, 27, 0, 0))
                .build());
        hechos.add(Hecho.builder()
                .titulo("Devastador Derrumbe en obra en construcción afecta a Presidencia Roque Sáenz Peña")
                .descripcion("Un grave derrumbe en obra en construcción se registró en Presidencia Roque Sáenz Peña, Chaco. El incidente generó preocupación entre las autoridades provinciales. El intendente local se ha trasladado al lugar para supervisar las operaciones.")
                .categoria(new Categoria("Derrumbe en obra en construcción"))
                .ubicacion(new Ubicacion(-26.780008, -60.458782))
                .fechaAcontecimiento(LocalDateTime.of(2016, 6, 4, 0, 0))
                .build());

        Mockito.doReturn(this.hechos).when(this.fuente).obtenerHechos(Set.of());
    }

    // Escenario 1

    @Test
    public void creacionDeColeccionMedianteCargaManualTest() {
        coleccion = new Coleccion("Colección prueba", "Esto es una prueba");

        coleccion.agregarFuente(fuente);

        Assertions.assertEquals(5, coleccion.obtenerHechos().size());
    }

    @Test
    public void criteriosDePertenenciaTest() {
        coleccion = new Coleccion("Colección prueba", "Esto es una prueba");

        coleccion.agregarFuente(fuente);

        var fechaInicio = LocalDateTime.of(2000, 1, 1, 0, 0);
        var fechaFinal = LocalDateTime.of(2010, 1, 1, 0, 0);

        FiltroStrategy filtroFecha = new FiltroFechaStrategy(fechaInicio, fechaFinal);

        coleccion.agregarCriterio(filtroFecha);

        Assertions.assertEquals(3, coleccion.obtenerHechos().size());

        var categoria = new Categoria("Caída de aeronave");

        FiltroStrategy filtroCategoria = new FiltroCategoriaStrategy(categoria);

        coleccion.agregarCriterio(filtroCategoria);

        Assertions.assertEquals(2, coleccion.obtenerHechos().size());
    }

    @Test
    public void filtrosDelVisualizador() {
        coleccion = new Coleccion("Colección prueba", "Esto es una prueba");

        coleccion.agregarFuente(fuente);

        var etiqueta1 = new Etiqueta("Caída de Aeronave");
        var etiquetas = Set.of(etiqueta1);
        var titulo = "un título";

        FiltroStrategy filtroEtiqueta = new FiltroEtiquetaStrategy(etiquetas); // TODO: Agregar al diagrama de clases
        FiltroStrategy filtroTitulo = new FiltroTituloStrategy(titulo);

        coleccion.agregarCriterio(filtroEtiqueta);
        coleccion.agregarCriterio(filtroTitulo);

        Assertions.assertEquals(0, coleccion.obtenerHechos().size());
    }

    @Test
    public void etiquetasTest() {
        this.hechos.forEach(hecho -> {
            if (hecho.getTitulo().equals("Caída de aeronave impacta en Olavarría")) {
                hecho.addEtiqueta(new Etiqueta("Olavarría"));
                hecho.addEtiqueta(new Etiqueta("Grave"));
            }
        });

        var hecho = this.hechos.stream().filter(h -> h.getTitulo().equals("Caída de aeronave impacta en Olavarría")).findFirst().get();

        Assertions.assertEquals(2, hecho.getEtiquetas().size());
    }

    // Escenario 2

    @Test
    public void importacionDeHechosPorCsv() {
        CsvReaderAdapter csvReaderAdapter = new LectorCsv();
        FuenteDeDatos fuenteCsv = new FuenteEstaticaCsv(csvReaderAdapter, "src/test/resources/csv/desastres_naturales_argentina.csv", ",");

        var hechosCsv = fuenteCsv.obtenerHechos(Set.of());

        Optional<Hecho> optional = hechosCsv.stream()
                .filter(h -> h.getTitulo().equals("Ráfagas de más de 100 km/h causa estragos en San Vicente, Misiones"))
                .findFirst();

        Assertions.assertTrue(optional.isPresent());
        var hecho = optional.get();

        Assertions.assertEquals("La región de San Vicente en Misiones sufrió los efectos de una intensa ráfagas de más de 100 km/h. El incidente obligando a evacuar a residentes de la zona. Se ha convocado al comité de crisis para coordinar las acciones de respuesta.", hecho.getDescripcion());
        Assertions.assertEquals("Ráfagas de más de 100 km/h", hecho.getCategoria().getNombre());
        Assertions.assertEquals(-27.029465, hecho.getUbicacion().getLatitud());
        Assertions.assertEquals(-54.436559, hecho.getUbicacion().getLongitud());
        Assertions.assertEquals(LocalDateTime.of(2007, 12, 21, 0, 0), hecho.getFechaAcontecimiento());
    }


    // Escenario 3

    @Test
    public void solicitudesDeEliminacion() {
        var hecho = Hecho.builder()
                .titulo("Brote de enfermedad contagiosa causa estragos en San Lorenzo, Santa Fe")
                .descripcion("Grave brote de enfermedad contagiosa ocurrió en las inmediaciones de San Lorenzo, Santa Fe. El incidente dejó varios heridos y daños materiales. Se ha declarado estado de emergencia en la región para facilitar la asistencia.")
                .categoria(new Categoria("Evento sanitario"))
                .ubicacion(new Ubicacion(-32.786098, -60.741543))
                .fechaAcontecimiento(LocalDateTime.of(2005, 7, 5, 0, 0))
                .build();

        var solicitud1 = new Solicitud("Solicitud de eliminación por datos erroneos", "", hecho, "Juan Perez");

        solicitud1.rechazar(); // TODO: Deberia pasarle el motivo???

        Assertions.assertEquals(Estado.RECHAZADA, solicitud1.getEstado());

        var solicitud2 = new Solicitud("Solicitud de eliminación por datos erroneos", "", hecho, "Juan Perez");

        solicitud2.aceptar();

        Assertions.assertEquals(Estado.ACEPTADA, solicitud2.getEstado());
    }

}*/
