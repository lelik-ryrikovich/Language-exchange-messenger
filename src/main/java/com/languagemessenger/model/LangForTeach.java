package com.languagemessenger.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Модель для таблицы Lang_For_Teach.
 * Языки, которые пользователь может преподавать.
 * Составной ключ (App_User, Language) используется для уникальной идентификации записи,
 * а также хранит уровень владения языком (Proficiency_Level).
 */
@Entity
@Table(name = "Lang_For_Teach")
@Data
@IdClass(LangForTeachId.class)
public class LangForTeach {
    /**
     * Ссылка на пользователя, часть составного первичного ключа.
     * Отношение многие-к-одному с таблицей App_User.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "App_User")
    private AppUser appUser;

    /**
     * Ссылка на язык, часть составного первичного ключа.
     * Отношение многие-к-одному с таблицей Language.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "Language")
    private Language language;

    /**
     * Ссылка на уровень владения языком.
     * Отношение многие-к-одному с таблицей Proficiency_Level.
     */
    @ManyToOne
    @JoinColumn(name = "Proficiency_Level")
    private ProficiencyLevel proficiencyLevel;
}