package com.languagemessenger.controller;

import com.languagemessenger.model.AppUser;
import com.languagemessenger.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для управления отображением списка чатов в веб-интерфейсе Language Messenger.
 * Обрабатывает запросы, связанные с просмотром чатов текущего пользователя,
 * используя данные из сервиса AppUserService.
 */
@Controller
@RequestMapping("/web/home")
@RequiredArgsConstructor
public class ChatsController {
    private final AppUserService appUserService;

    /**
     * Отображает страницу со списком чатов текущего пользователя.
     * Получает данные пользователя и его чаты из сервиса AppUserService,
     * добавляет их в модель для рендеринга шаблона "chats".
     *
     * @param model объект модели Spring MVC для передачи данных в представление
     * @return имя шаблона Thymeleaf ("chats"), который будет отображён
     */
    @GetMapping("/chats")
    public String showChats(Model model) {
        AppUser user = appUserService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("chats", appUserService.getUserChats(user));
        return "chats";
    }
}