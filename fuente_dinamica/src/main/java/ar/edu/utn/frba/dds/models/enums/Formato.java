package ar.edu.utn.frba.dds.models.enums;

public enum Formato {
  TEXTO,
  IMAGEN,
  AUDIO,
  VIDEO;

  public static Formato fromString(String formato) {
      try {

        return Formato.valueOf(formato.toUpperCase());
      } catch (Exception e) {
        throw new IllegalArgumentException("Formato desconocido: " + formato);
      }
  }
}
