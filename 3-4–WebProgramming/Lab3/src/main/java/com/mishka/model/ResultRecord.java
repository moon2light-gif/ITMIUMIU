package com.mishka.model;

/**
 * Класс-запись {@code ResultRecord} представляет собой конечный, валидный результат записи,
 * который должен быть отправлен браузеру и отображён в таблице результатов.
 * @author mikhailkramar
 *
 * @param x - координата X точки
 * @param y - координата Y точки
 * @param r - параметр R
 * @param isHit true - попадание, false - промах
 * @param currentTime - время отправки запроса
 * @param executionTime - время выполнения скрипта на стороне веб-сервера
 */
public record ResultRecord(
        double x,
        double y,
        double r,
        boolean isHit,
        long currentTime,
        double executionTime
) {}
