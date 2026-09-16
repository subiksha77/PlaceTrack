package com.placetrack.backend.service.impl;

import com.placetrack.backend.dto.*;
import com.placetrack.backend.entity.*;
import com.placetrack.backend.exception.ResourceNotFoundException;
import com.placetrack.backend.exception.UnauthorizedException;
import com.placetrack.backend.repository.InterviewQuestionRepository;
import com.placetrack.backend.repository.MockInterviewAnswerRepository;
import com.placetrack.backend.repository.MockInterviewSessionRepository;
import com.placetrack.backend.repository.StudentRepository;
import com.placetrack.backend.service.MockInterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MockInterviewServiceImpl implements MockInterviewService {

    private final MockInterviewSessionRepository sessionRepository;
    private final MockInterviewAnswerRepository answerRepository;
    private final InterviewQuestionRepository questionRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public MockInterviewResponse startSession(User user, MockInterviewStartRequest request) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found."));

        MockInterviewSession session = new MockInterviewSession();
        session.setStudent(student);
        session.setTargetCompany(request.targetCompany());
        session.setTargetRole(request.targetRole());
        session.setInterviewType(request.interviewType() != null
                ? MockInterviewSession.InterviewType.valueOf(request.interviewType().toUpperCase())
                : MockInterviewSession.InterviewType.MIXED);
        session.setDifficulty(request.difficulty() != null
                ? MockInterviewSession.Difficulty.valueOf(request.difficulty().toUpperCase())
                : MockInterviewSession.Difficulty.MEDIUM);
        session.setStatus(MockInterviewSession.Status.IN_PROGRESS);
        session.setStartedAt(LocalDateTime.now());

        int questionCount = (request.questionCount() != null && request.questionCount() > 0)
                ? Math.min(request.questionCount(), 20) : 5;

        // Pick random questions from the DB
        List<InterviewQuestion> allQuestions = questionRepository.findByActiveTrue();
        Collections.shuffle(allQuestions);
        List<InterviewQuestion> picked = allQuestions.stream().limit(questionCount).toList();

        session.setTotalQuestions(picked.size());
        MockInterviewSession saved = sessionRepository.save(session);

        // Create answer placeholders
        List<MockInterviewAnswer> answers = new ArrayList<>();
        for (int i = 0; i < picked.size(); i++) {
            InterviewQuestion q = picked.get(i);
            MockInterviewAnswer answer = new MockInterviewAnswer();
            answer.setSession(saved);
            answer.setQuestionId(q.getId());
            answer.setQuestionText(q.getQuestion());
            answer.setCategory(q.getCategory());
            answer.setDifficulty(q.getDifficulty());
            answer.setSampleAnswer(q.getAnswer());
            answers.add(answer);
        }
        answerRepository.saveAll(answers);
        saved.setAnswers(answers);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public MockAnswerResponse submitAnswer(User user, Long sessionId, MockAnswerRequest request) {
        MockInterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mock interview session not found."));

        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found."));

        if (!session.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("This is not your interview session.");
        }

        List<MockInterviewAnswer> answers = session.getAnswers();
        int idx = request.questionIndex();
        if (idx < 0 || idx >= answers.size()) {
            throw new IllegalArgumentException("Invalid question index: " + idx);
        }

        MockInterviewAnswer answer = answers.get(idx);
        answer.setStudentAnswer(request.studentAnswer());
        answer.setAnsweredAt(LocalDateTime.now());

        // Simple keyword-based scoring
        int score = scoreAnswer(request.studentAnswer(), answer.getSampleAnswer());
        answer.setScore(score);
        answer.setIsStrong(score >= 60);
        answer.setFeedback(generateFeedback(score));
        answerRepository.save(answer);

        return toAnswerResponse(answer, idx);
    }

    @Override
    @Transactional
    public MockInterviewResponse completeSession(User user, Long sessionId) {
        MockInterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mock interview session not found."));

        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found."));

        if (!session.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("This is not your interview session.");
        }

        session.setStatus(MockInterviewSession.Status.COMPLETED);
        session.setCompletedAt(LocalDateTime.now());

        List<MockInterviewAnswer> answers = session.getAnswers();
        int totalScore = 0;
        int answered = 0;
        List<String> strongTopics = new ArrayList<>();
        List<String> weakTopics = new ArrayList<>();

        for (MockInterviewAnswer a : answers) {
            if (a.getScore() != null) {
                totalScore += a.getScore();
                answered++;
                String topic = a.getCategory() != null ? a.getCategory().getLabel() : "General";
                if (a.getIsStrong() != null && a.getIsStrong()) {
                    strongTopics.add(topic);
                } else {
                    weakTopics.add(topic);
                }
            }
        }

        session.setScore(answered > 0 ? totalScore / answered : 0);
        session.setStrengths(strongTopics.isEmpty() ? "Keep practicing!" : String.join(", ", strongTopics.stream().distinct().toList()));
        session.setImprovements(weakTopics.isEmpty() ? "Great performance!" : String.join(", ", weakTopics.stream().distinct().toList()));
        session.setSuggestedTopics(weakTopics.isEmpty() ? "None - you're doing great!" : String.join(", ", weakTopics.stream().distinct().limit(3).toList()));

        return toResponse(sessionRepository.save(session));
    }

    @Override
    public List<MockInterviewResponse> getMySessions(User user) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found."));
        return sessionRepository.findByStudentIdOrderByStartedAtDesc(student.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    public MockInterviewResponse getSession(Long sessionId) {
        MockInterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mock interview session not found."));
        return toResponse(session);
    }

    private int scoreAnswer(String studentAnswer, String correctAnswer) {
        if (studentAnswer == null || studentAnswer.isBlank()) return 0;
        if (correctAnswer == null || correctAnswer.isBlank()) return 50;

        Set<String> studentWords = Arrays.stream(studentAnswer.toLowerCase().split("\\W+"))
                .filter(w -> w.length() > 2).collect(Collectors.toSet());
        Set<String> correctWords = Arrays.stream(correctAnswer.toLowerCase().split("\\W+"))
                .filter(w -> w.length() > 2).collect(Collectors.toSet());

        if (correctWords.isEmpty()) return 50;

        long matches = studentWords.stream().filter(correctWords::contains).count();
        int raw = (int) (matches * 100 / correctWords.size());
        return Math.min(raw, 100);
    }

    private String generateFeedback(int score) {
        if (score >= 80) return "Excellent answer! You covered most of the key points.";
        if (score >= 60) return "Good answer. You got the main concepts but could expand on some points.";
        if (score >= 40) return "Fair attempt. Consider studying the key concepts more thoroughly.";
        if (score >= 20) return "Needs improvement. Review the topic and try to cover the essential points.";
        return "Your answer needs significant improvement. Review the correct answer carefully.";
    }

    private MockInterviewResponse toResponse(MockInterviewSession s) {
        List<MockAnswerResponse> answerResponses = new ArrayList<>();
        if (s.getAnswers() != null) {
            for (int i = 0; i < s.getAnswers().size(); i++) {
                answerResponses.add(toAnswerResponse(s.getAnswers().get(i), i));
            }
        }
        return new MockInterviewResponse(
                s.getId(),
                s.getStudent().getId(),
                s.getStudent().getStudentName(),
                s.getTargetCompany(),
                s.getTargetRole(),
                s.getInterviewType().name(),
                s.getDifficulty().name(),
                s.getStatus().name(),
                s.getTotalQuestions(),
                s.getScore(),
                s.getStartedAt() != null ? s.getStartedAt().toString() : null,
                s.getCompletedAt() != null ? s.getCompletedAt().toString() : null,
                s.getStrengths(),
                s.getImprovements(),
                s.getSuggestedTopics(),
                answerResponses
        );
    }

    private MockAnswerResponse toAnswerResponse(MockInterviewAnswer a, int index) {
        return new MockAnswerResponse(
                a.getId(),
                index,
                a.getQuestionText(),
                a.getCategory() != null ? a.getCategory().name() : null,
                a.getDifficulty() != null ? a.getDifficulty().name() : null,
                a.getStudentAnswer(),
                a.getSampleAnswer(),
                a.getScore(),
                a.getFeedback()
        );
    }
}
