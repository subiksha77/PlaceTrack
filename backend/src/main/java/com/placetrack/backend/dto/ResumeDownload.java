package com.placetrack.backend.dto;

import org.springframework.core.io.Resource;

/** A resume file ready to stream to the owner. */
public record ResumeDownload(String fileName, String contentType, Resource resource) {}
