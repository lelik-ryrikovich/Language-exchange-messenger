package com.languagemessenger.controller;

import com.languagemessenger.model.City;
import com.languagemessenger.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер REST API для управления данными о городах в приложении Language Messenger.
 * Предоставляет эндпоинты для получения списка городов по стране и всех городов.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CityController {
    private final CityRepository cityRepository;

    /**
     * Возвращает список городов, соответствующих указанной стране.
     * Использует параметр запроса для фильтрации городов по названию страны.
     *
     * @param country название страны для фильтрации городов
     * @return {@link ResponseEntity} со списком объектов {@link City}, соответствующих стране
     */
    @GetMapping("/cities")
    public ResponseEntity<List<City>> getCitiesByCountry(@RequestParam String country) {
        return ResponseEntity.ok(cityRepository.findByCountryCountryName(country));
    }

    /**
     * Возвращает полный список всех городов, доступных в базе данных.
     *
     * @return {@link ResponseEntity} со списком всех объектов {@link City}
     */
    @GetMapping("/all-cities")
    public ResponseEntity<List<City>> getAllCities() {
        return ResponseEntity.ok(cityRepository.findAll());
    }
}