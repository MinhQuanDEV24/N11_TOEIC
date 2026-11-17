package com.toeic.nhom11.vocabulary.controller;

import com.toeic.nhom11.vocabulary.dto.VocabularyQuiz;
import com.toeic.nhom11.vocabulary.model.VocabularyUserPreferences;
import com.toeic.nhom11.vocabulary.service.VocabularyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/vocabulary")
public class VocabularyController {
    private final VocabularyService service;

    public VocabularyController(VocabularyService service) {
        this.service = service;
    }

    @ModelAttribute void common(Model model) {
        model.addAttribute("activePage", "vocabulary");
        model.addAttribute("pageTitle", "Học Từ Vựng TOEIC");
    }

    @GetMapping
    public String index(Principal user, Model model) {
        String email = user == null ? null : user.getName();
        var topics = service.topicSummaries(email);
        model.addAttribute("topics", topics);
        model.addAttribute("flashcard", service.randomWord().orElse(null));
        var scores = user == null ? Collections.<Long, java.math.BigDecimal>emptyMap()
            : service.bestScores(user.getName());
        model.addAttribute("bestScores", scores);
        int totalLessons = topics.stream().mapToInt(topic -> topic.getTotalLessons()).sum();
        int completedLessons = topics.stream().mapToInt(topic -> topic.getCompletedLessons()).sum();
        int totalStars = topics.stream().mapToInt(topic -> topic.getTotalStars()).sum();
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("completedLessons", completedLessons);
        model.addAttribute("totalStars", totalStars);
        model.addAttribute("overallProgress", totalLessons == 0 ? 0
            : completedLessons * 100.0 / totalLessons);
        model.addAttribute("preferences", preferences(email));
        model.addAttribute("userEmail", email);
        return "vocabulary/index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable long id, Principal user, Model model) {
        var topic = service.topic(id, false);
        model.addAttribute("topic", topic);
        model.addAttribute("words", service.words(id));
        var lessons = service.lessonSummaries(id, user == null ? null : user.getName());
        model.addAttribute("lessons", lessons);
        model.addAttribute("lessonCount", lessons.size());
        model.addAttribute("completedLessons", lessons.stream()
            .filter(item -> "COMPLETED".equals(item.getState())).count());
        model.addAttribute("totalStars", lessons.stream().mapToInt(item -> item.getStars()).sum());
        model.addAttribute("placementPassed",
            service.placementPassed(id, user == null ? null : user.getName()));
        model.addAttribute("preferences", preferences(user == null ? null : user.getName()));
        model.addAttribute("userEmail", user == null ? null : user.getName());
        model.addAttribute("pageTitle", topic.getTitle());
        return "vocabulary/detail";
    }

    @GetMapping("/{topicId}/lessons/{lessonId}")
    public String lesson(@PathVariable long topicId, @PathVariable long lessonId,
                         Principal user, Model model) {
        String email = user == null ? null : user.getName();
        model.addAttribute("topic", service.topic(topicId, false));
        model.addAttribute("lesson", service.beginLesson(topicId, lessonId, email));
        model.addAttribute("words", service.lessonWords(topicId, lessonId));
        model.addAttribute("preferences", preferences(email));
        model.addAttribute("userEmail", email);
        return "vocabulary/lesson";
    }

    @GetMapping("/{topicId}/placement")
    public String placement(@PathVariable long topicId, Principal user,
                            HttpSession session, Model model) {
        VocabularyQuiz quiz = service.startPlacementQuiz(topicId, user == null ? null : user.getName());
        session.setAttribute(quizSessionKey(quiz), quiz);
        model.addAttribute("topic", service.topic(topicId, false));
        model.addAttribute("quiz", quiz);
        model.addAttribute("quizAction", "/vocabulary/" + topicId + "/placement/submit");
        model.addAttribute("preferences", preferences(user == null ? null : user.getName()));
        return "vocabulary/quiz";
    }

    @GetMapping("/{topicId}/lessons/{lessonId}/quiz/{mode}")
    public String lessonQuiz(@PathVariable long topicId, @PathVariable long lessonId,
                             @PathVariable String mode, Principal user,
                             HttpSession session, Model model) {
        String quizMode;
        if ("dictation".equalsIgnoreCase(mode)) {
            quizMode = "DICTATION";
        } else if ("exercise".equalsIgnoreCase(mode)) {
            quizMode = "EXERCISE";
        } else {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND);
        }
        VocabularyQuiz quiz = service.startLessonQuiz(topicId, lessonId, quizMode,
            user == null ? null : user.getName());
        session.setAttribute(quizSessionKey(quiz), quiz);
        model.addAttribute("topic", service.topic(topicId, false));
        model.addAttribute("lesson", service.beginLesson(topicId, lessonId,
            user == null ? null : user.getName()));
        model.addAttribute("quiz", quiz);
        model.addAttribute("quizAction", "/vocabulary/" + topicId + "/lessons/" + lessonId
            + "/quiz/" + mode + "/submit");
        model.addAttribute("preferences", preferences(user == null ? null : user.getName()));
        return "vocabulary/quiz";
    }

    @PostMapping("/{topicId}/placement/submit")
    public String submitPlacement(@PathVariable long topicId, @RequestParam Map<String, String> params,
                                  Principal user, HttpSession session) {
        return submitLearningQuiz(topicId, null, "PLACEMENT", params, user, session);
    }

    @PostMapping("/{topicId}/lessons/{lessonId}/quiz/{mode}/submit")
    public String submitLessonQuiz(@PathVariable long topicId, @PathVariable long lessonId,
                                   @PathVariable String mode, @RequestParam Map<String, String> params,
                                   Principal user, HttpSession session) {
        return submitLearningQuiz(topicId, lessonId, "LESSON", params, user, session);
    }

    private String submitLearningQuiz(long topicId, Long lessonId, String expectedType,
                                     Map<String, String> params,
                                     Principal user, HttpSession session) {
        String token = params.get("quizToken");
        Object pending = token == null ? null : session.getAttribute("vocabularyQuiz:" + token);
        if (!(pending instanceof VocabularyQuiz quiz) || quiz.getTopicId() != topicId
            || !expectedType.equals(quiz.getType())
            || !Objects.equals(lessonId, quiz.getLessonId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "Bài kiểm tra đã hết hạn. Hãy bắt đầu lại.");
        }
        Map<String, String> responses = new HashMap<>();
        params.forEach((key, value) -> {
            if (key.startsWith("answer_") || key.startsWith("dictation_")) {
                responses.put(key, value);
            }
        });
        session.removeAttribute("vocabularyQuiz:" + token);
        long attemptId = service.submitLearningQuiz(quiz,
            user == null ? null : user.getName(), responses);
        return "redirect:/vocabulary/attempts/" + attemptId;
    }

    @PostMapping("/preferences")
    @ResponseBody
    public Map<String, Object> updatePreferences(@RequestParam String theme,
                                                 @RequestParam String fontSize,
                                                 @RequestParam String language,
                                                 Principal user) {
        VocabularyUserPreferences preferences = new VocabularyUserPreferences();
        preferences.setTheme(theme);
        preferences.setFontSize(fontSize);
        preferences.setLanguage(language);
        service.updatePreferences(user == null ? null : user.getName(), preferences);
        return Map.of("success", true);
    }

    @PostMapping("/{id}/submit")
    public String submit(@PathVariable long id, @RequestParam Map<String, String> params,
        Principal user, RedirectAttributes flash) {
        Map<Long, Long> answers = new HashMap<>();
        try {
            for (var entry : params.entrySet())
                if (entry.getKey().startsWith("answer_"))
                    answers.put(Long.parseLong(entry.getKey().substring(7)),
                        Long.parseLong(entry.getValue()));
            long attempt = service.submit(id, user.getName(), answers);
            return "redirect:/vocabulary/attempts/" + attempt;
        } catch (NumberFormatException ex) {
            flash.addFlashAttribute("error", "Dữ liệu đáp án không hợp lệ.");
            return "redirect:/vocabulary/" + id;
        }
    }

    @GetMapping("/attempts/{id}")
    public String result(@PathVariable long id, Principal user, Model model) {
        model.addAttribute("attempt", service.attempt(id, user.getName()));
        model.addAttribute("answers", service.answers(id, user.getName()));
        model.addAttribute("preferences", service.preferences(user.getName()));
        model.addAttribute("userEmail", user.getName());
        return "vocabulary/result";
    }

    @GetMapping("/history")
    public String history(Principal user, Model model) {
        model.addAttribute("attempts", service.history(user.getName()));
        model.addAttribute("preferences", service.preferences(user.getName()));
        model.addAttribute("userEmail", user.getName());
        return "vocabulary/history";
    }

    @PostMapping("/{termId}/progress")
    @ResponseBody
    public Map<String, Object> updateProgress(@PathVariable long termId,
        @RequestParam String status, Principal user) {
        service.updateProgress(termId, user.getName(), status);
        return Map.of("success", true);
    }

    @PostMapping("/{termId}/favorite")
    @ResponseBody
    public Map<String, Object> toggleFavorite(@PathVariable long termId,
        @RequestParam boolean favorite, Principal user) {
        service.markFavorite(termId, user.getName(), favorite);
        return Map.of("success", true, "favorite", favorite);
    }

    private VocabularyUserPreferences preferences(String email) {
        return email == null ? new VocabularyUserPreferences() : service.preferences(email);
    }

    private String quizSessionKey(VocabularyQuiz quiz) {
        return "vocabularyQuiz:" + quiz.getToken();
    }
}
