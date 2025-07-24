package com.languagemessenger.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Модель для таблицы Chat.
 * Чат между двумя пользователями (1:1).
 * Связан с участниками через Chat_Member и сообщениями через Message.
 * Поддерживает имя чата для идентификации.
 */
@Entity
@Table(name = "Chat")
@Data
public class Chat {
    /**
     * Уникальный идентификатор чата, генерируемый автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Integer id;

    /**
     * Название чата, ограниченное 32 символами, используется для идентификации.
     */
    @Column(name = "chat_name", length = 32)
    private String chatName;

    /**
     * Список участников чата, связанных через сущность ChatMember.
     * Отношение один-ко-многим, управляемое обратной стороной (mappedBy).
     */
    @OneToMany(mappedBy = "chat")
    private List<ChatMember> members;

    /**
     * Список сообщений, связанных с чатом.
     * Отношение один-ко-многим, управляемое обратной стороной (mappedBy).
     */
    @OneToMany(mappedBy = "chat")
    private List<Message> messages;
}