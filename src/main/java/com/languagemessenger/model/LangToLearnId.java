package com.languagemessenger.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Класс для составного ключа Lang_To_Learn.
 * Используется как встраиваемый идентификатор для связи пользователя и языка,
 * который он хочет изучать, реализует интерфейс Serializable для поддержки сериализации.
 */
@Embeddable
@Data
public class LangToLearnId implements java.io.Serializable {
    /**
     * Идентификатор пользователя, часть составного ключа.
     */
    private Integer appUser;

    /**
     * Название языка, часть составного ключа.
     */
    private String language;
}