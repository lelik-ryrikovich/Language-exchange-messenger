package com.languagemessenger.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Модель для таблицы Language.
 * Язык, связанный с Lang_For_Teach и Lang_To_Learn.
 */
@Entity
@Table(name = "Language")
@Data
public class Language {
    /**
     * Название языка.
     */
    @Id
    @Column(name = "language_name", length = 32)
    private String languageName;
}
