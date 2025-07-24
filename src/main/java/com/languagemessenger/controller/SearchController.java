package com.languagemessenger.controller;

import com.languagemessenger.model.AppUser;
import com.languagemessenger.model.Chat;
import com.languagemessenger.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления поиском пользователей и созданием чатов в Language Messenger.
 * Обрабатывает отображение страницы поиска и REST API для поиска пользователей и создания чатов.
 */
@Controller
@RequestMapping("/web/home")
@RequiredArgsConstructor
public class SearchController {
    private final AppUserService appUserService;

    /**
     * Отображает страницу поиска пользователей.
     * Добавляет в модель текущего пользователя, его языки для изучения,
     * уровни владения, страны и отображаемые названия уровней.
     *
     * @param model объект модели Spring MVC для передачи данных в представление
     * @return имя шаблона Thymeleaf ("search"), который будет отображён
     */
    @GetMapping("/search")
    public String showSearch(Model model) {
        AppUser user = appUserService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("languagesToLearn", appUserService.getLanguagesToLearn(user));
        model.addAttribute("allProficiencyLevels", appUserService.getAllProficiencyLevels());
        model.addAttribute("allCountries", appUserService.getAllCountries());
        model.addAttribute("proficiencyLevelDisplayNames", appUserService.getProficiencyLevelDisplayNames());
        return "search";
    }

    /**
     * Обрабатывает AJAX-запрос на поиск пользователей по языку, уровню владения и стране.
     * Возвращает JSON-ответ со списком подходящих пользователей или ошибкой.
     *
     * @param languageName название языка для поиска
     * @param proficiencyLevelName уровень владения языком
     * @param country название страны (по умолчанию "Any" для любого значения)
     * @return {@link ResponseEntity} с мапой, содержащей статус и список пользователей
     * @throws IllegalArgumentException если параметры некорректны
     */
    @PostMapping("/api/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam("language") String languageName,
            @RequestParam("proficiencyLevel") String proficiencyLevelName,
            @RequestParam(value = "country", defaultValue = "Any") String country
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            AppUser currentUser = appUserService.getCurrentUser();
            List<AppUser> matchingUsers = appUserService.findMatchingUsers(
                    currentUser,
                    languageName,
                    proficiencyLevelName,
                    country
            );
            response.put("success", true);
            response.put("users", matchingUsers);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Обрабатывает AJAX-запрос на создание чата между текущим пользователем и указанным пользователем.
     * Возвращает JSON-ответ с ID созданного чата или ошибкой.
     *
     * @param userId идентификатор пользователя, с которым создаётся чат
     * @return {@link ResponseEntity} с мапой, содержащей статус и ID чата
     * @throws IllegalArgumentException если пользователь не найден
     */
    @PostMapping("/api/create-chat")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createChat(@RequestParam("userId") Integer userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            AppUser currentUser = appUserService.getCurrentUser();
            AppUser otherUser = appUserService.getUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Chat chat = appUserService.createChat(currentUser, otherUser);
            response.put("success", true);
            response.put("chatId", chat.getId());
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}