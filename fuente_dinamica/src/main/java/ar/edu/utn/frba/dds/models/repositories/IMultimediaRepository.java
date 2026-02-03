package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Multimedia;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface IMultimediaRepository {
    Multimedia guardar(MultipartFile file) throws IOException;
    InputStream obtener(String nombreArchivo) throws IOException;
    void eliminar(String nombreArchivo) throws IOException;
    boolean existe(String nombreArchivo);
}
