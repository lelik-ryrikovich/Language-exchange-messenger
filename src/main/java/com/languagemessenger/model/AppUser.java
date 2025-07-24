package com.languagemessenger.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import java.time.LocalDate;

/**
 * Модель для таблицы App_User.
 * Пользователь мессенджера с уникальными email, login, nickname.
 * Связан с городом (City) через внешний ключ.
 * Содержит данные для аутентификации, географической привязки и RSA-ключей для шифрования.
 */
@Entity
@Table(name = "App_User")
@Data
public class AppUser {
    /**
     * Уникальный идентификатор пользователя, генерируемый автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_id")
    private Integer id;

    /**
     * Никнейм пользователя, уникальный и ограниченный 32 символами.
     */
    @Column(name = "nickname", unique = true, length = 32)
    private String nickname;

    /**
     * Логин пользователя, уникальный и ограниченный 32 символами.
     */
    @Column(name = "login", unique = true, length = 32)
    private String login;

    /**
     * Хранимый пароль пользователя, зашифрованный с использованием BCrypt, ограниченный 255 символами.
     */
    @Column(name = "password", length = 255)
    private String password;

    /**
     * Связь с сущностью City (город пользователя) через внешний ключ.
     */
    @ManyToOne
    @JoinColumn(name = "City")
    private City city;

    /**
     * Дата рождения пользователя.
     */
    @Column(name = "day_of_birth")
    private LocalDate dayOfBirth;

    /**
     * Дата регистрации пользователя в системе.
     */
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    /**
     * Электронная почта пользователя, уникальная и соответствующая формату email, ограниченная 254 символами.
     */
    @Email
    @Column(name = "email", unique = true, length = 254)
    private String email;

    /**
     * Язык перевода по умолчанию для пользователя, ограниченный 10 символами, с начальным значением "en".
     */
    @Column(name = "translation_language", length = 10)
    private String translationLanguage = "en"; // По умолчанию английский

    /**
     * Публичный ключ RSA пользователя, закодированный в Base64, хранится как текст.
     */
    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey;

    /**
     * Приватный ключ RSA пользователя, закодированный в Base64, хранится как текст.
     */
    @Column(name = "private_key", columnDefinition = "TEXT")
    private String privateKey;
}