package com.placetrack.backend.service.impl;

import com.placetrack.backend.dto.InterviewQuestionRequest;
import com.placetrack.backend.dto.InterviewQuestionResponse;
import com.placetrack.backend.entity.InterviewQuestion;
import com.placetrack.backend.exception.ResourceNotFoundException;
import com.placetrack.backend.repository.InterviewQuestionRepository;
import com.placetrack.backend.service.InterviewQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InterviewQuestionServiceImpl implements InterviewQuestionService {

    private final InterviewQuestionRepository questionRepository;

    @Override
    public List<InterviewQuestionResponse> getAll(String category, String difficulty, String search) {
        InterviewQuestion.Category cat = (category != null && !category.isBlank())
                ? InterviewQuestion.Category.from(category) : null;
        InterviewQuestion.Difficulty diff = (difficulty != null && !difficulty.isBlank())
                ? InterviewQuestion.Difficulty.from(difficulty) : null;
        String s = (search != null && !search.isBlank()) ? search : null;
        return questionRepository.searchAndFilter(cat, diff, s)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public InterviewQuestionResponse getById(Long id) {
        InterviewQuestion q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question not found with id: " + id));
        return toResponse(q);
    }

    @Override
    @Transactional
    public InterviewQuestionResponse create(InterviewQuestionRequest request) {
        InterviewQuestion q = new InterviewQuestion();
        q.setCategory(InterviewQuestion.Category.from(request.category()));
        q.setDifficulty(InterviewQuestion.Difficulty.from(request.difficulty()));
        q.setQuestion(request.question());
        q.setAnswer(request.answer());
        q.setExplanation(request.explanation());
        q.setTopic(request.topic());
        q.setActive(true);
        return toResponse(questionRepository.save(q));
    }

    @Override
    @Transactional
    public InterviewQuestionResponse update(Long id, InterviewQuestionRequest request) {
        InterviewQuestion q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question not found with id: " + id));
        if (request.category() != null) q.setCategory(InterviewQuestion.Category.from(request.category()));
        if (request.difficulty() != null) q.setDifficulty(InterviewQuestion.Difficulty.from(request.difficulty()));
        if (request.question() != null) q.setQuestion(request.question());
        if (request.answer() != null) q.setAnswer(request.answer());
        if (request.explanation() != null) q.setExplanation(request.explanation());
        if (request.topic() != null) q.setTopic(request.topic());
        return toResponse(questionRepository.save(q));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        InterviewQuestion q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question not found with id: " + id));
        q.setActive(false);
        questionRepository.save(q);
    }

    @Override
    public List<Map<String, String>> getCategories() {
        List<Map<String, String>> categories = new ArrayList<>();
        for (InterviewQuestion.Category c : InterviewQuestion.Category.values()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("value", c.name());
            map.put("label", c.getLabel());
            categories.add(map);
        }
        return categories;
    }

    private InterviewQuestionResponse toResponse(InterviewQuestion q) {
        return new InterviewQuestionResponse(
                q.getId(),
                q.getCategory().name(),
                q.getCategory().getLabel(),
                q.getDifficulty().name(),
                q.getQuestion(),
                q.getAnswer(),
                q.getExplanation(),
                q.getTopic(),
                q.isActive()
        );
    }
}
