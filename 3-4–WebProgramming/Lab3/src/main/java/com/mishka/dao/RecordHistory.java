package com.mishka.dao;

import com.mishka.model.ResultRecord;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Класс {@code RecordHistory} предназначен для хранения истории всех отправленных ResultRecord'ов
 *
 * @author mikhailkramar
 */
public class RecordHistory {
    private static final String HISTORY_ATTRIBUTE = "history";

    public static void save(HttpSession session, ResultRecord record) {
        List<ResultRecord> history = (List<ResultRecord>) session.getAttribute(HISTORY_ATTRIBUTE);

        if (history == null) {
            history = new CopyOnWriteArrayList<>();
            session.setAttribute(HISTORY_ATTRIBUTE, history);
        }

        history.add(record);
    }

    public static List<ResultRecord> getAll(HttpSession session) {
        if (session == null) {
            return Collections.emptyList();
        }

        List<ResultRecord> history = (List<ResultRecord>) session.getAttribute(HISTORY_ATTRIBUTE);

        if (history == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    public static void clear(HttpSession session) {
        if (session != null) {
            session.removeAttribute(HISTORY_ATTRIBUTE);
        }
    }
}
