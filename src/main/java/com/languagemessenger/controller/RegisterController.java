package com.languagemessenger.controller;

import com.languagemessenger.exception.ValidationException;
import com.languagemessenger.model.AppUser;
import com.languagemessenger.repository.CountryRepository;
import com.languagemessenger.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Контроллер для управления процессом регистрации пользователей в Language Messenger.
 * Обрабатывает отображение формы регистрации и выполнение регистрации с валидацией данных.
 */
@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class RegisterController {
    private final AppUserService userService;
    private final CountryRepository countryRepository;

    /**
     * Отображает форму регистрации для нового пользователя.
     * Инициализирует объект пользователя и добавляет список стран для выбора.
     *
     * @param model объект модели Spring MVC для передачи данных в представление
     * @return имя шаблона Thymeleaf ("register"), который будет отображён
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new AppUser());
        model.addAttribute("countries", countryRepository.findAll());
        return "register";
    }

    /**
     * Обрабатывает запрос на регистрацию нового пользователя.
     * Выполняет валидацию данных и регистрирует пользователя, связывая его с указанным городом.
     * При успешной регистрации перенаправляет на страницу логина, при ошибке возвращает форму с ошибками.
     *
     * @param user объект пользователя с введёнными данными
     * @param cityId идентификатор города, выбранного пользователем
     * @param model объект модели Spring MVC для передачи данных и ошибок в представление
     * @return перенаправление на "/web/login" при успехе или "register" при ошибке
     * @throws ValidationException если данные не проходят валидацию
     * @throws IllegalArgumentException если город не найден или произошла другая ошибка
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute AppUser user, @RequestParam Integer cityId, Model model) {
        try {
            userService.register(user, cityId);
            return "redirect:/web/login"; // После успешной регистрации редирект на логин
        } catch (ValidationException e) {
            model.addAttribute("errors", e.getErrors()); // Передаём список ошибок
            model.addAttribute("user", user); // Сохраняем введённые данные
            model.addAttribute("countries", countryRepository.findAll());
            return "register"; // Возвращаем страницу с ошибками
        } catch (IllegalArgumentException e) {
            model.addAttribute("errors", List.of(e.getMessage())); // Для совместимости со старыми исключениями
            model.addAttribute("user", user);
            model.addAttribute("countries", countryRepository.findAll());
            return "register";
        }
    }
}