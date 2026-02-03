package ar.edu.utn.frba.dds.models;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Coleccion {
  private String id;
  private String titulo;
  private String descripcion;
  private List<Fuente> fuentes;
  private int cantSolicitudesSpam;
  private List<FiltroDTOSalida> criterios;
  private String algoritmoConsenso;
  private String estado;

  public List<String> getCriterioLabels() {
    List<String> etiquetas = new ArrayList<>();
    if (criterios == null)
      return etiquetas;

    for (FiltroDTOSalida c : criterios) {
      String etiqueta = formatearCriterio(c);
      etiquetas.add(etiqueta);
    }
    return etiquetas;
  }

  private String formatearCriterio(FiltroDTOSalida c) {
    String tipo = c.getTipoFiltro();

    switch (tipo) {
      case "FILTRO_CATEGORIA":
        return "Categoría: " + c.getValor();
      case "FILTRO_PROVINCIA":
        return "Provincia: " + c.getValor();
      case "FILTRO_MUNICIPIO":
        return "Municipio: " + c.getValor();
      case "FILTRO_FECHA_ACONTECIMIENTO":
        return "Ocurrido entre: " + c.getFechaInicio() + " y " + c.getFechaFin();
      case "FILTRO_FECHA_REPORTE":
        return "Reportado entre: " + c.getFechaInicio() + " y " + c.getFechaFin();
      case "FILTRO_FUENTE":
        return "Fuente: " + c.getTipoFuente();
      default:
        return c.getValor() != null ? c.getValor() : tipo;
    }
  }
}
