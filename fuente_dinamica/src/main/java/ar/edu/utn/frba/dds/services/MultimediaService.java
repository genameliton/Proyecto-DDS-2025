package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Multimedia;
import ar.edu.utn.frba.dds.models.repositories.IMultimediaJpaRepository;
import ar.edu.utn.frba.dds.models.repositories.IMultimediaRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@Service
public class MultimediaService {

    private final IMultimediaRepository multimediaRepository;
    private final IMultimediaJpaRepository multimediaJpaRepository;

    public MultimediaService(IMultimediaRepository multimediaRepository,
            IMultimediaJpaRepository multimediaJpaRepository) {
        this.multimediaRepository = multimediaRepository;
        this.multimediaJpaRepository = multimediaJpaRepository;
    }

    public Multimedia guardarArchivo(MultipartFile file) throws IOException {
        Multimedia multimedia = multimediaRepository.guardar(file);
        return multimediaJpaRepository.save(multimedia);
    }

    public InputStream obtenerArchivo(Long id) throws IOException {
        Multimedia multimedia = multimediaJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe multimedia con id " + id));

        String nombreArchivo = Paths.get(multimedia.getRuta()).getFileName().toString();
        return multimediaRepository.obtener(nombreArchivo);
    }

    public void eliminarArchivo(Long id) throws IOException {
        Multimedia multimedia = multimediaJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe multimedia con id " + id));

        String nombreArchivo = Paths.get(multimedia.getRuta()).getFileName().toString();
        multimediaRepository.eliminar(nombreArchivo);
        multimediaJpaRepository.delete(multimedia);
    }

    public boolean existeArchivo(Long id) {
        return multimediaJpaRepository.existsById(id);
    }
}
