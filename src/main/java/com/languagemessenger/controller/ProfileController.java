package com.languagemessenger.controller;

import com.languagemessenger.model.AppUser;
import com.languagemessenger.model.Chat;
import com.languagemessenger.repository.ChatRepository;
import com.languagemessenger.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для управления профилем пользователя и связанными данными в Language Messenger.
 * Обрабатывает REST API для получения ключей, участников чатов и обновления языков,
 * а также отображает HTML-страницу профиля.
 */
@Controller
@RequestMapping("/web/home")
@RequiredArgsConstructor
public class ProfileController {
    private final AppUserService appUserService;
    private final ChatRepository chatRepository;

    /**
     * Возвращает публичный ключ указанного пользователя через REST API.
     *
     * @param userId идентификатор пользователя, чей публичный ключ требуется
     * @return {@link ResponseEntity} с мапой, содержащей публичный ключ,
     *         или 404, если пользователь не найден
     */
    @GetMapping("/api/users/{userId}/public-key")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getPublicKey(@PathVariable Integer userId) {
        return appUserService.getUserById(userId)
                .map(user -> ResponseEntity.ok(Map.of("publicKey", user.getPublicKey())))
                .orElseGet(() -> new ResponseEntity<>(ResponseEntity.notFound().build().getStatusCode()));
    }

    /**
     * Возвращает публичный ключ текущего пользователя через REST API.
     *
     * @return {@link ResponseEntity} с map, содержащей публичный ключ текущего пользователя
     */
    @GetMapping("/api/user/public-key")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getCurrentUserPublicKey() {
        AppUser user = appUserService.getCurrentUser();
        return ResponseEntity.ok(Map.of("publicKey", user.getPublicKey()));
    }

    /**
     * Возвращает приватный ключ текущего пользователя через REST API.
     *
     * @return {@link ResponseEntity} с мапой, содержащей приватный ключ текущего пользователя
     */
    @GetMapping("/api/user/private-key")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getPrivateKey() {
        AppUser user = appUserService.getCurrentUser();
        return ResponseEntity.ok(Map.of("privateKey", user.getPrivateKey()));
    }

    /**
     * Возвращает список участников указанного чата через REST API.
     *
     * @param chatId идентификатор чата, участники которого запрашиваются
     * @return {@link ResponseEntity} со списком мап, содержащих ID и никнеймы участников
     * @throws IllegalArgumentException если чат не найден
     */
    @GetMapping("/api/chats/{chatId}/members")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getChatMembers(@PathVariable int chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));
        List<Map<String, Object>> members = chat.getMembers().stream()
                .map(member -> Map.of(
                        "userId", (Object) member.getAppUser().getId(),
                        "nickname", member.getAppUser().getNickname()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }

    /**
     * Отображает HTML-страницу профиля текущего пользователя.
     * Добавляет в модель данные пользователя, языки для изучения и преподавания,
     * а также доступные языки и уровни владения.
     *
     * @param model объект модели Spring MVC для передачи данных в представление
     * @return имя шаблона Thymeleaf ("profile"), который будет отображён
     */
    @GetMapping("/profile")
    public String showProfile(Model model) {
        AppUser user = appUserService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("languagesToLearn", appUserService.getLanguagesToLearn(user));
        model.addAttribute("languagesToTeach", appUserService.getLanguagesToTeach(user));
        model.addAttribute("allLanguages", appUserService.getAllLanguages());
        model.addAttribute("allProficiencyLevels", appUserService.getAllProficiencyLevels());
        model.addAttribute("proficiencyLevelDisplayNames", appUserService.getProficiencyLevelDisplayNames());
        model.addAttribute("availableTranslationLanguages", appUserService.getAvailableTranslationLanguages());
        model.addAttribute("currentTranslationLanguage", appUserService.getTranslationLanguage(user));
        return "profile";
    }

    /**
     * Добавляет язык для изучения текущего пользователя через AJAX-запрос.
     * Возвращает JSON-ответ с результатом операции и обновлённым списком языков.
     *
     * @param languageName название языка для изучения
     * @param proficiencyLevelName уровень владения языком
     * @return {@link ResponseEntity} с мапой, содержащей статус и обновлённые данные
     */
    @PostMapping("/api/profile/add-language-to-learn")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addLanguageToLearnAjax(
            @RequestParam("language") String languageName,
            @RequestParam("proficiencyLevel") String proficiencyLevelName
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            AppUser user = appUserService.getCurrentUser();
            appUserService.addLanguageToLearn(user, languageName, proficiencyLevelName);
            response.put("success", true);
            response.put("languagesToLearn", appUserService.getLanguagesToLearn(user));
        } catch (IllegalStateException | IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Добавляет язык для преподавания текущего пользователя через AJAX-запрос.
     * Возвращает JSON-ответ с результатом операции и обновлённым списком языков.
     *
     * @param languageName название языка для преподавания
     * @param proficiencyLevelName уровень владения языком
     * @return {@link ResponseEntity} с мапой, содержащей статус и обновлённые данные
     */
    @PostMapping("/api/profile/add-language-to-teach")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addLanguageToTeachAjax(
            @RequestParam("language") String languageName,
            @RequestParam("proficiencyLevel") String proficiencyLevelName
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            AppUser user = appUserService.getCurrentUser();
            appUserService.addLanguageToTeach(user, languageName, proficiencyLevelName);
            response.put("success", true);
            response.put("languagesToTeach", appUserService.getLanguagesToTeach(user));
        } catch (IllegalStateException | IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Обновляет уровень владения языком для изучения через AJAX-запрос.
     * Возвращает JSON-ответ с результатом операции и обновлённым списком языков.
     *
     * @param languageName название языка для обновления
     * @param proficiencyLevelName новый уровень владения языком
     * @return {@link ResponseEntity} с мапой, содержащей статус и обновлённые данные
     */
    @PostMapping("/api/profile/update-language-to-learn")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateLanguageToLearnAjax(
            @RequestParam("language") String languageName,
            @RequestParam("proficiencyLevel") String proficiencyLevelName
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            AppUser user = appUserService.getCurrentUser();
            appUserService.updateLanguageToLearn(user, languageName, proficiencyLevelName);
            response.put("success", true);
            response.put("languagesToLearn", appUserService.getLanguagesToLearn(user));
        } catch (IllegalStateException | IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Обновляет уровень владения языком для преподавания через AJAX-запрос.
     * Возвращает JSON-ответ с результатом операции и обновлённым списком языков.
     *
     * @param languageName название языка для обновления
     * @param proficiencyLevelName новый уровень владения языком
     * @return {@link ResponseEntity} с мапой, содержащей статус и обновлённые данные
     */
    @PostMapping("/api/profile/update-language-to-teach")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateLanguageToTeachAjax(
            @RequestParam("language") String languageName,
            @RequestParam("proficiencyLevel") String proficiencyLevelName
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            AppUser user = appUserService.getCurrentUser();
            appUserService.updateLanguageToTeach(user, languageName, proficiencyLevelName);
            response.put("success", true);
            response.put("languagesToTeach", appUserService.getLanguagesToTeach(user));
        } catch (IllegalStateException | IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Удаляет язык из списка для изучения через AJAX-запрос.
     * Возвращает JSON-ответ с результатом операции и обновлённым списком языков.
     *
     * @param languageName название языка для удаления
     * @return {@link ResponseEntity} с мапой, содержащей статус и обновлённые данные
     */
    @PostMapping("/api/profile/delete-language-to-learn")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteLanguageToLearnAjax(
            @RequestParam("language") String languageName
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            AppUser user = appUserService.getCurrentUser();
            appUserService.deleteLanguageToLearn(user, languageName);
            response.put("success", true);
            response.put("languagesToLearn", appUserService.getLanguagesToLearn(user));
        } catch (IllegalStateException | IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Удаляет язык из списка для преподавания через AJAX-запрос.
     * Возвращает JSON-ответ с результатом операции и обновлённым списком языков.
     *
     * @param languageName название языка для удаления
     * @return {@link ResponseEntity} с мапой, содержащей статус и обновлённые данные
     */
    @PostMapping("/api/profile/delete-language-to-teach")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteLanguageToTeachAjax(
            @RequestParam("language") String languageName
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            AppUser user = appUserService.getCurrentUser();
            appUserService.deleteLanguageToTeach(user, languageName);
            response.put("success", true);
            response.put("languagesToTeach", appUserService.getLanguagesToTeach(user));
        } catch (IllegalStateException | IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Обновляет язык перевода для текущего пользователя через AJAX-запрос.
     * Возвращает JSON-ответ с результатом операции и новым языком перевода.
     *
     * @param translationLanguage новый язык перевода
     * @return {@link ResponseEntity} с мапой, содержащей статус и обновлённый язык
     */
    @PostMapping("/api/profile/update-translation-language")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTranslationLanguageAjax(
            @RequestParam("translationLanguage") String translationLanguage
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            AppUser user = appUserService.getCurrentUser();
            appUserService.setTranslationLanguage(user, translationLanguage);
            response.put("success", true);
            response.put("currentTranslationLanguage", translationLanguage);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}