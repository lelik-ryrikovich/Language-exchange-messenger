package com.languagemessenger.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Модель для таблицы Message.
 * Сообщение в чате, связанное с отправителем и чатом.
 * Поддерживает зашифрованные данные (AES) с инициализационным вектором (IV) и ключами RSA
 * для обеспечения безопасности обмена сообщениями.
 */
@Entity
@Table(name = "Message")
@Data
public class Message {
    /**
     * Уникальный идентификатор сообщения, генерируемый автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer id;

    /**
     * Ссылка на отправителя сообщения.
     * Отношение многие-к-одному с таблицей App_User.
     */
    @ManyToOne
    @JoinColumn(name = "Sender")
    private AppUser sender;

    /**
     * Ссылка на чат, к которому относится сообщение.
     * Отношение многие-к-одному с таблицей Chat.
     */
    @ManyToOne
    @JoinColumn(name = "Chat")
    private Chat chat;

    /**
     * Дата и время отправки сообщения с учётом часового пояса.
     */
    @Column(name = "date_of_sending")
    private ZonedDateTime dateOfSending;

    /**
     * Зашифрованный текст сообщения, закодированный с использованием алгоритма AES,
     * хранится как текст.
     */
    @Column(name = "encrypted_text", columnDefinition = "TEXT")
    private String encryptedText;

    /**
     * Зашифрованный AES-ключ для получателя, закодированный с использованием RSA,
     * хранится как текст.
     */
    @Column(name = "encrypted_aes_key_recipient", columnDefinition = "TEXT")
    private String encryptedAesKeyRecipient;

    /**
     * Зашифрованный AES-ключ для отправителя, закодированный с использованием RSA,
     * хранится как текст.
     */
    @Column(name = "encrypted_aes_key_sender", columnDefinition = "TEXT")
    private String encryptedAesKeySender;

    /**
     * Инициализационный вектор (IV) для алгоритма AES, закодированный в Base64,
     * ограниченный 24 символами (соответствует 16 байтам).
     */
    @Column(name = "iv", length = 24)
    private String iv;
}