package com.languagemessenger.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

/**
 * Класс для составного ключа Chat_Member.
 * Используется как встраиваемый идентификатор для связи между чатом и пользователем,
 * реализует интерфейс Serializable для поддержки сериализации.
 */
@Embeddable
@Data
class ChatMemberId implements java.io.Serializable {
    /**
     * Идентификатор чата, часть составного ключа.
     */
    private Integer chat;

    /**
     * Идентификатор пользователя, часть составного ключа.
     */
    private Integer appUser;
}