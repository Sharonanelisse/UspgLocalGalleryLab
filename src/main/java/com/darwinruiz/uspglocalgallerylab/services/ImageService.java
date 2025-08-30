package com.darwinruiz.uspglocalgallerylab.services;

import com.darwinruiz.uspglocalgallerylab.dto.UploadResult;
import com.darwinruiz.uspglocalgallerylab.repositories.IFileRepository;
import com.darwinruiz.uspglocalgallerylab.util.ImageValidator;
import com.darwinruiz.uspglocalgallerylab.util.NamePolicy;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImageService {
    private final IFileRepository repo;

    public ImageService(IFileRepository repo) {
        this.repo = repo;
    }

    /**
     * Implementación del upload de imágenes:
     *  - Iterar partes "file"
     *  - Normalizar nombre (NamePolicy.normalize)
     *  - Validar (ImageValidator.isValid)
     *  - Crear subcarpeta con fecha (NamePolicy.datedSubdir)
     *  - Guardar en el repositorio
     *  - Contar subidos/rechazados
     */
    public UploadResult uploadLocalImages(Collection<Part> parts) throws IOException, ServletException {
        int ok = 0, bad = 0;
        List<String> save = new ArrayList<>();

        for (Part p : parts) {
            if (!"file".equals(p.getName()) || p.getSize() == 0) continue;

            String submitted = p.getSubmittedFileName();
            String fileName = Paths.get(submitted).getFileName().toString();

            fileName = NamePolicy.normalize(fileName);

            try (InputStream in = p.getInputStream()) {

                if (ImageValidator.isValid(fileName, in)) {

                    String subdir = NamePolicy.datedSubdir(LocalDate.now());

                    repo.save(subdir, fileName, in);

                    ok++;
                    save.add(subdir + "/" + fileName);
                } else {
                    bad++;
                }
            } catch (Exception ex) {
                bad++;
            }
        }
        return new UploadResult(ok, bad, save);
    }
}