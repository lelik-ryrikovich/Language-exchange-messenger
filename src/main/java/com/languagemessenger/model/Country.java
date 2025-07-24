package com.languagemessenger.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * Модель для таблицы Country.
 * Страна, связанная с городами.
 * Используется для географической привязки пользователей через города в приложении Language Messenger.
 */
@Entity
@Table(name = "Country")
@Data
public class Country {
    /**
     * Название страны, используемое как первичный ключ, ограниченное 32 символами.
     */
    @Id
    @Column(name = "country_name", length = 32)
    private String countryName;

    /**
     * Список городов, связанных с этой страной.
     * Отношение один-ко-многим, управляемое обратной стороной (mappedBy).
     * Аннотация @JsonManagedReference управляет сериализацией для избежания рекурсии.
     */
    @OneToMany(mappedBy = "country")
    @JsonManagedReference
    private List<City> cities;
}