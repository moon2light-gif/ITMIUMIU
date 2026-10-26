package com.mishka.fcgi.utility;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Класс {@code RecordHistory} предназначен для хранения истории всех отправленных ResultRecord'ов
 * @author mikhailkramar
 */
public class RecordHistory {
    public static final List<ResultRecord> history = new CopyOnWriteArrayList<>();

    /**
     * Конструктор класса RecordHistory
     * @param pointRecord полностью корректная запись точки
     */
    public static void save(ResultRecord pointRecord) {
        history.add(pointRecord);
    }

    /**
     * Метод для получения всей истории отправленных точек
     * @return все записи в формате read-only List
     */
    public static List<ResultRecord> getAll() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Метод для очистки всей истории запросов
     */
    public static void clear() {
        history.clear();
    }
}
