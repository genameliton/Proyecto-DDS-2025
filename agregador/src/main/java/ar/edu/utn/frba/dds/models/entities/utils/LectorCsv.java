package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.entities.adapters.CsvReaderAdapter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorCsv implements CsvReaderAdapter {
  @Override
  public List<Object> readCsv(String path, String separator) {
    List<Object> objetos = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String linea;
      while ((linea = br.readLine()) != null) {
        String[] datosFila = linea.split(separator);
        objetos.add(datosFila); // cada fila es un String[]
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return objetos;
  }
}