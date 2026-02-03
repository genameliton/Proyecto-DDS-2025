package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.HechoCsv;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ExtractorHechosCSV {

  public List<Hecho> obtenerHechos(MultipartFile urlCsv) throws IOException {
    char separador;
    HeaderColumnNameMappingStrategy<HechoCsv> strategy = new HeaderColumnNameMappingStrategy<>();
    strategy.setType(HechoCsv.class);

    BufferedReader reader = new BufferedReader(new InputStreamReader(urlCsv.getInputStream()));
    String primeraLinea = reader.readLine();
    int comas = primeraLinea.split(",").length;
    int puntosYComa = primeraLinea.split(";").length;
    int tabulaciones = primeraLinea.split("\t").length;
    if (puntosYComa > comas && puntosYComa > tabulaciones) {
      separador = ';';
    } else if (tabulaciones > comas) {
      separador = '\t';
    } else {
      separador = ',';
    }
    CsvToBean<HechoCsv> csvToBean = new CsvToBeanBuilder<HechoCsv>(
        new InputStreamReader(urlCsv.getInputStream()))
        .withSeparator(separador)
        .withMappingStrategy(strategy)
        .withIgnoreLeadingWhiteSpace(true)
        .build();

    List<HechoCsv> hechos = csvToBean.parse();
    List<Hecho> hechosCsv = new ArrayList<>();
    hechos.forEach(hecho -> {
      LocalDateTime fechaHecho = hecho.getFecha().atStartOfDay();
      Hecho hechoDTO = new Hecho(hecho.getTitulo(), hecho.getDescripcion(), hecho.getCategoria(), hecho.getLatitud(), hecho.getLongitud(), fechaHecho, LocalDateTime.now());
      hechosCsv.add(hechoDTO);
    });
    return hechosCsv;

  }
  public Map<String, Object> obtenerDatosValidacion(MultipartFile urlCsv) {
    Map<String, Object> response = new HashMap<>();
    int count = 0;
    try {
      response.put("esCsv", urlCsv.getOriginalFilename().endsWith(".csv"));
      BufferedReader reader = new BufferedReader(new InputStreamReader(urlCsv.getInputStream()));
      while (reader.readLine() != null) {
        count++;
      }
      response.put("registros", count-=1);
    } catch (Exception e) {
      response.put("error", e.getMessage());
    }
    return response;
  }

}
