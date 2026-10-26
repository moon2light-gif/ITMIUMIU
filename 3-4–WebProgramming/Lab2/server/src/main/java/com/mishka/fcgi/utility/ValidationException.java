package com.mishka.fcgi.utility;

import java.util.Map;

/**
 * Класс {@code ValidationException} представляет собой исключение,
 * выбрасываемое при валидации введённых в форму параметров и содержащее информацию о конкретных ошибках.
 * @author mikhailkramar
 */
public class ValidationException extends Exception {
    private final Map<String, String> errors;

    /**
     * Конструктор исключения
     * @param errors Map, содержащая набор ошибок, для корректного отображения на фронте в js
     */
    public ValidationException(Map<String, String> errors) {
        super("Ошибка валидации!");
        this.errors = errors;
    }

    /**
     * Метод получения всех ошибок, полученных во время валидации
     * @return Map, содержащая набор ошибок
     */
    public Map<String, String> getErrors() {
        return errors;
    }
}
