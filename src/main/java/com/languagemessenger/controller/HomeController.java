package com.languagemessenger.controller;

import com.languagemessenger.service.AppUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для обработки главной страницы веб-интерфейса Language Messenger.
 * Управляет перенаправлением пользователя на страницу поиска при доступе к корневому пути.
 */
@Controller
@RequestMapping("/web/home")
public class HomeController {
    private final AppUserService appUserService;

    /**
     * Конструктор класса, инициализирующий сервис пользователей.
     *
     * @param appUserService сервис для работы с данными пользователей
     */
    public HomeController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * Обрабатывает GET-запрос к корневому пути и перенаправляет пользователя
     * на страницу поиска (/web/home/search).
     *
     * @return перенаправление на "/web/home/search"
     */
    @GetMapping
    public String home() {
        return "redirect:/web/home/search";
    }
}