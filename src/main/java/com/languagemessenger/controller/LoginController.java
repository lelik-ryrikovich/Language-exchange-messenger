package com.languagemessenger.controller;

import com.languagemessenger.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Контроллер для управления процессом аутентификации пользователей в Language Messenger.
 * Обрабатывает отображение формы логина, выполнение входа с генерацией JWT-токена
 * и выход из системы с удалением токена.
 */
@Controller
@RequestMapping("/web")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Конструктор класса, инициализирующий менеджер аутентификации и утилиту JWT.
     *
     * @param authenticationManager менеджер аутентификации Spring Security
     * @param jwtUtil утилита для генерации и управления JWT-токенами
     */
    public LoginController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Отображает форму логина для пользователя.
     *
     * @return имя шаблона Thymeleaf ("login"), который будет отображён
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    /**
     * Обрабатывает запрос на вход в систему.
     * Аутентифицирует пользователя с использованием менеджера Spring Security,
     * генерирует JWT-токен и сохраняет его в HTTP-only cookie.
     * При ошибке аутентификации возвращает форму логина с сообщением об ошибке.
     *
     * @param username имя пользователя для аутентификации
     * @param password пароль пользователя
     * @param model объект модели Spring MVC для передачи данных в представление
     * @param response объект ответа HTTP для установки cookie с JWT
     * @return перенаправление на "/web/home" при успехе или "login" при ошибке
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            String jwt = jwtUtil.generateToken(authentication.getName());
            Cookie jwtCookie = new Cookie("JWT", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(10 * 60 * 60); // 10 часов
            response.addCookie(jwtCookie);
            return "redirect:/web/home";
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Invalid login or password");
            return "login";
        }
    }

    /**
     * Обрабатывает выход пользователя из системы.
     * Удаляет JWT-токен, устанавливая cookie с нулевым сроком действия,
     * и перенаправляет на страницу логина.
     *
     * @param response объект ответа HTTP для удаления cookie с JWT
     * @return перенаправление на "/web/login"
     */
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("JWT", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Удаляет cookie
        response.addCookie(jwtCookie);
        return "redirect:/web/login";
    }
}