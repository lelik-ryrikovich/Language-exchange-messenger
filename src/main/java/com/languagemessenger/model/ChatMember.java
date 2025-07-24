package com.languagemessenger.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Модель для таблицы Chat_Member.
 * Связывает чат и пользователя (участника чата).
 * Составной первичный ключ (Chat, App_User) используется для уникальной идентификации записи.
 */
@Entity
@Table(name = "Chat_Member")
@Data
@IdClass(ChatMemberId.class)
public class ChatMember {
    /**
     * Ссылка на чат, часть составного первичного ключа.
     * Отношение многие-к-одному с таблицей Chat.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "Chat")
    private Chat chat;

    /**
     * Ссылка на пользователя, часть составного первичного ключа.
     * Отношение многие-к-одному с таблицей App_User.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "App_User")
    private AppUser appUser;
}