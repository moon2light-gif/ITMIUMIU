package com.mishka.fcgi;

import com.fastcgi.FCGIInterface;
import com.mishka.fcgi.utility.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс {@code FSGIServer} представляет собой обработку полученного запроса и отправку результата Apache-серверу
 * @author mikhailkramar
 */
public class FCGIServer {

    public static void main(String[] args) {
        FCGIInterface fcgiInterface = new FCGIInterface();
        while (fcgiInterface.FCGIaccept() >= 0) {
            // Засекаем время на старте
            long startTime = System.nanoTime();

            try {
                // Получаем строку запроса, которая следует за знаком '?'
                String queryParams = System.getProperty("QUERY_STRING");
                Map<String, String> rawParams = parseQuery(queryParams);

                // Если пришёл запрос на очистку истории запросов
                if ("clear".equals(rawParams.get("action"))) {
                    RecordHistory.clear();
                    sendJsonResponse(200, historyToJson(RecordHistory.getAll()));
                    continue;
                }

                // Если пришёл запрос на получение таблицы '/fcgi?action=history'
                if ("history".equals(rawParams.get("action"))) {
                    sendJsonResponse(200, historyToJson(RecordHistory.getAll()));
                    continue;
                }

                // Получаем запись точки только со значениями x, y, r
                ParsedData rawPointRecord = RequestValidator.validateForm(rawParams);

                // Выполняем проверку попадания точки в заданную область
                boolean isHit = checkArea(rawPointRecord.x(), rawPointRecord.y(), rawPointRecord.r());

                // Время выполнения скрипта в миллисекундах
                double executionTime = (System.nanoTime()-startTime)/1_000_000.0;

                // Собираем финальную запись о запросе
                ResultRecord finalRecord = new ResultRecord(
                        rawPointRecord.x(),
                        rawPointRecord.y(),
                        rawPointRecord.r(),
                        isHit,
                        System.currentTimeMillis(),
                        executionTime
                );
                RecordHistory.save(finalRecord);

                sendJsonResponse(200, recordToJson(finalRecord));

            } catch (ValidationException ex) {
                sendJsonResponse(400, errorsToJson(ex.getErrors()));
            } catch (Exception ex) {
                ex.printStackTrace();
                sendJsonResponse(500, errorsToJson(Map.of("server", "Ошибка сервера")));
            }
        }
    }

    /**
     * Метод, который получает на вход параметры запроса и преобразует их в понятную структуру
     * @param searchParams строка вида "x=1.5&y=2&r=3", содержащая параметры запроса
     * @return {@code Map<String, String>}, содержащая запись из searchParams в виде Map
     */
    public static Map<String, String> parseQuery(String searchParams) {
        Map<String, String> map = new HashMap<>();
        if (searchParams == null || searchParams.isEmpty()) return map;

        for (String pair : searchParams.split("&")) {
            if (pair.isEmpty()) continue;
            String[] kv = pair.split("=", 2);   // 2 = не больше двух частей
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String val = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            map.put(key, val);
        }
        return map;
    }

    /**
     * Метод, определяющий попадание точки в заданную область
     * @param x координата X точки
     * @param y координата Y точки
     * @param r значение параметра R
     * @return true при попадании, иначе - false
     */
    public static boolean checkArea(int x, double y, int r) {
       return BatmanArea.contains(x, y, r);
    }

    /**
     * Метод для отправки HTTP-response
     * @param statusCode код ошибки
     * @param jsonBody json-строка, которую необходимо отправить в качестве body HTTP-ответа
     */
    public static void sendJsonResponse(int statusCode, String jsonBody) {
        byte[] body = jsonBody.getBytes(StandardCharsets.UTF_8);
        String reason = switch (statusCode) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            default -> "Internal Server Error";
        };
        String header = "HTTP/1.1 " + statusCode + " " + reason + "\r\n"
                + "Content-Type: application/json; charset=UTF-8\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "Connection: close\r\n\r\n";
        System.out.print(header);
        System.out.write(body, 0, body.length);
        System.out.flush();
    }

    /**
     * Метод, позволяющий получить json формат объекта ResultRecord
     * @param finalRecord - исходный объект ResultRecord
     * @return запись в виде json-строки
     */
    public static String recordToJson(ResultRecord finalRecord) {
        // Locale.US для отображения точки вместо запятой в дробных числах
        return String.format(Locale.US,
                "{\"x\":%d,\"y\":%s,\"r\":%d, \"hit\":%b, \"timestamp\":%d, \"executionTime\": %.3f}",
                finalRecord.x(), finalRecord.y(), finalRecord.r(), finalRecord.isHit(), finalRecord.currentTime(), finalRecord.executionTime()
        );
    }

    /**
     * Метод получения json-строки из истории всех запросов, представленной List
     * @param history - List, состоящий из объектов ResultRecord
     * @return исходный List в виде json-строки
     */
    public static String historyToJson(List<ResultRecord> history) {
        return history.stream()
                .map(FCGIServer::recordToJson)
                .collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * Метод получения json-строки из Map
     * @param errors Map ошибок, которые необходимо показать на фронте
     * @return исходная Map errors в виде json-строки
     */
    private static String errorsToJson(Map<String, String> errors) {
        String body = errors.entrySet().stream()
                .map(e -> "\"" + escape(e.getKey()) + "\":\"" + escape(e.getValue()) + "\"")
                .collect(Collectors.joining(","));
        return "{\"errors\":{" + body + "}}";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}
