package ar.edu.utn.frba.dds.models.repositories.impl;

import ar.edu.utn.frba.dds.models.entities.Multimedia;
import ar.edu.utn.frba.dds.models.enums.Formato;
import ar.edu.utn.frba.dds.models.repositories.IMultimediaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Repository
public class MultimediaFileSystemRepository implements IMultimediaRepository {

    @Value("${app.multimedia.upload-dir}")
    private String uploadDir;

    @Value("${app.multimedia.base-url}")
    private String baseUrl;

    @Override
    public Multimedia guardar(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo multimedia está vacío.");
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        Files.createDirectories(uploadPath);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = obtenerExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + "." + extension;

        Path destino = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        Formato formato = determinarFormato(extension);
        String rutaAcceso = baseUrl + "/media/" + uniqueFilename;

        return Multimedia.builder()
                .nombre(originalFilename)
                .ruta(rutaAcceso)
                .formato(formato)
                .build();
    }

    @Override
    public InputStream obtener(String nombreArchivo) throws IOException {
        Path archivo = Paths.get(uploadDir).resolve(nombreArchivo).normalize();
        if (!Files.exists(archivo)) {
            throw new IOException("Archivo no encontrado: " + nombreArchivo);
        }
        return Files.newInputStream(archivo, StandardOpenOption.READ);
    }

    @Override
    public void eliminar(String nombreArchivo) throws IOException {
        Path archivo = Paths.get(uploadDir).resolve(nombreArchivo).normalize();
        Files.deleteIfExists(archivo);
    }

    @Override
    public boolean existe(String nombreArchivo) {
        Path archivo = Paths.get(uploadDir).resolve(nombreArchivo).normalize();
        return Files.exists(archivo);
    }

    private String obtenerExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }

    private Formato determinarFormato(String extension) {
        return switch (extension) {
            case "jpg", "jpeg", "png", "gif", "bmp" -> Formato.IMAGEN;
            case "mp4", "avi", "mov", "wmv" -> Formato.VIDEO;
            case "mp3", "wav", "ogg" -> Formato.AUDIO;
            case "txt", "pdf", "doc", "docx" -> Formato.TEXTO;
            default -> throw new IllegalArgumentException("Extensión de archivo no soportada: " + extension);
        };
    }
}