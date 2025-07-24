package com.languagemessenger.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Модель для таблицы City.
 * Город, связанный с страной (Country).
 * Используется для географической привязки пользователей в приложении Language Messenger.
 */
@Entity
@Table(name = "City")
@Data
public class City {
    /**
     * Уникальный идентификатор города, генерируемый автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private Integer id;

    /**
     * Название города, ограниченное 32 символами.
     */
    @Column(name = "city_name", length = 32)
    private String cityName;

    /**
     * Ссылка на страну, к которой относится город.
     * Отношение многие-к-одному, управляемое через внешний ключ.
     * Аннотация @JsonBackReference предотвращает бесконечную рекурсию при сериализации.
     */
    @ManyToOne
    @JoinColumn(name = "Country")
    @JsonBackReference
    private Country country;
}