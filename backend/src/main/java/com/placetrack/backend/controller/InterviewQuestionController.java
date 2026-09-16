package com.placetrack.backend.controller;

import com.placetrack.backend.dto.InterviewQuestionRequest;
import com.placetrack.backend.dto.InterviewQuestionResponse;
import com.placetrack.backend.service.InterviewQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interview-questions")
@RequiredArgsConstructor
public class InterviewQuestionController {

    private final InterviewQuestionService interviewQuestionService;

    @GetMapping
    public ResponseEntity<List<InterviewQuestionResponse>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(interviewQuestionService.getAll(category, difficulty, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewQuestionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(interviewQuestionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<InterviewQuestionResponse> create(@RequestBody InterviewQuestionRequest request) {
        return ResponseEntity.ok(interviewQuestionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterviewQuestionResponse> update(
            @PathVariable Long id,
            @RequestBody InterviewQuestionRequest request) {
        return ResponseEntity.ok(interviewQuestionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        interviewQuestionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, String>>> getCategories() {
        return ResponseEntity.ok(interviewQuestionService.getCategories());
    }
}
