package com.languagemessenger.exception;

import java.util.List;

/**
 * Кастомное исключение для обработки ошибок валидации данных в приложении Language Messenger.
 * Содержит сообщение об ошибке и список конкретных ошибок валидации для передачи в клиентскую часть.
 */
public class ValidationException extends RuntimeException {
    private final List<String> errors;

    /**
     * Создаёт новое исключение валидации с указанным сообщением и списком ошибок.
     *
     * @param message общее сообщение об ошибке
     * @param errors список конкретных ошибок валидации
     */
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Возвращает список конкретных ошибок валидации, связанных с этим исключением.
     *
     * @return неизменяемый список строк с ошибками
     */
    public List<String> getErrors() {
        return errors;
    }
}