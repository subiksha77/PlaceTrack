package com.placetrack.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/** Local file storage for student resumes. Files live in
 *  {@value #UPLOAD_DIR} and are streamed only to their owner through
 *  {@code /api/profile/resume/download}. Paths are validated to stay inside
 *  the storage root (no path traversal). */
public interface ResumeStorageService {

    /** Save a resume for the given student and return the server-side absolute path. */
    String store(Long studentId, MultipartFile file);

    /** Stream a previously stored resume as a downloadable resource. */
    Resource loadAsResource(String storagePath);

    /** Delete a stored resume file (ignore failures – DB record is the source of truth). */
    void delete(String storagePath);

    String UPLOAD_DIR = "uploads";
}
