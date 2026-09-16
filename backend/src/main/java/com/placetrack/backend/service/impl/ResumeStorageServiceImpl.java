package com.placetrack.backend.service.impl;

import com.placetrack.backend.service.ResumeStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/** Stores resumes on the server filesystem under {@value #UPLOAD_DIR}. */
@Service
public class ResumeStorageServiceImpl implements ResumeStorageService {

    private static final String UPLOAD_DIR = "uploads";
    private final Path root;

    public ResumeStorageServiceImpl(@Value("${placetrack.upload.dir:./uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir, UPLOAD_DIR).toAbsolutePath().normalize();
    }

    @Override
    public String store(Long studentId, MultipartFile file) {
        if (studentId == null || file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Resume upload failed");
        }
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not create resume directory", e);
        }
        String originalName = file.getOriginalFilename();
        String safeName = sanitize(originalName);
        String stored = studentId + "-" + UUID.randomUUID().toString() + "." + safeName;
        Path target = root.resolve(stored);
        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save resume", e);
        }
        return target.toFile().getAbsolutePath();
    }

    @Override
    public Resource loadAsResource(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            throw new IllegalArgumentException("No resume found");
        }
        Path path = Paths.get(storagePath).toAbsolutePath().normalize();
        if (!path.startsWith(root) || !Files.exists(path)) {
            throw new IllegalArgumentException("Resume not found");
        }
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new IllegalArgumentException("Resume not readable");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resume", e);
        }
    }

    @Override
    public void delete(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) return;
        try {
            Path path = Paths.get(storagePath).toAbsolutePath().normalize();
            if (path.startsWith(root) && Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException ignored) {
            // best-effort
        }
    }

    private String sanitize(String original) {
        if (original == null || original.isBlank()) return "pdf";
        int dot = original.lastIndexOf('.');
        return dot < 0 ? "pdf" : original.substring(dot + 1).toLowerCase();
    }
}
