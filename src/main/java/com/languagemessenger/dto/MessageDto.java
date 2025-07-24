package com.languagemessenger.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) для передачи данных сообщений между клиентом и сервером
 * в приложении Language Messenger. Содержит информацию о сообщении, включая идентификатор,
 * отправителя, временную метку, переведённый текст, обнаруженный язык и зашифрованные данные.
 */
@Data
public class MessageDto {
    /**
     * Уникальный идентификатор сообщения.
     */
    private Integer id;

    /**
     * Идентификатор отправителя сообщения.
     */
    private Integer senderId;

    /**
     * Никнейм отправителя сообщения.
     */
    private String senderName;

    /**
     * Временная метка отправки сообщения в формате строки.
     */
    private String timestamp;

    /**
     * Переведённый текст сообщения, полученный через внешний API перевода.
     */
    private String translatedContent;

    /**
     * Определённый язык исходного текста сообщения.
     */
    private String detectedLanguage;

    /**
     * Зашифрованный текст сообщения, используя алгоритм AES.
     */
    private String encryptedText;

    /**
     * Зашифрованный AES-ключ для получателя, используя алгоритм RSA.
     */
    private String encryptedAesKeyRecipient;

    /**
     * Зашифрованный AES-ключ для отправителя, используя алгоритм RSA.
     */
    private String encryptedAesKeySender;

    /**
     * Вектор инициализации (IV) для алгоритма AES.
     */
    private String iv;
}