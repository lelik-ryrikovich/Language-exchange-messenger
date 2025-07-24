package com.languagemessenger.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Модель для таблицы Proficiency_Level.
 * Уровень владения языком.
 */
@Entity
@Table(name = "Proficiency_Level")
@Data
public class ProficiencyLevel {
    /**
     * Название уровня владения языком.
     */
    @Id
    @Column(name = "proficiency_level_name", length = 32)
    private String proficiencyLevelName;
}
