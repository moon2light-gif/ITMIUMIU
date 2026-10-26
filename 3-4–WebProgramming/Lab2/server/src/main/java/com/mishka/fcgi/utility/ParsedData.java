package com.mishka.fcgi.utility;

/**
 * Класс-запись {@code ParsedData} представляет собой промежуточный результат,
 * полученный после валидации полученных веб-сервером значений.
 * @param x - полученное значение X
 * @param y - полученное значение Y
 * @param r - полученное значение R
 */
public record ParsedData(
        int x,
        double y,
        int r
) {}
