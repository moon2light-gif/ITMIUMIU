package com.mishka.fcgi.utility;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс {@code RequestValidator} предоставляет метод валидации полученных веб-сервером значений в строке запроса,
 * позволяющий также сообщить об ошибках при вводе значений в форму.
 * @author mikhailkramar
 */
public class RequestValidator {
    /**
     * @param rawParams Map, содержащая введённые в форму параметры в виде объекта String
     * @return объект ParsedData, содержащий значения параметров в правильном формате
     * @throws ValidationException исключение, содержащее сообщение об ошибках при вводе значений
     */
    public static ParsedData validateForm(Map<String, String> rawParams) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        int x = 0;
        double y = 0;
        int r = 0;

        // Координата X
        String xString = rawParams.get("x");
        if (xString == null || xString.isBlank() || xString.equals("null")) {
            errors.put("x", "Выберите значение X!");
        }
        else {
            try {
                x = Integer.parseInt(xString.trim());
                if (x < -3 || x > 5) {
                    errors.put("x", "X должен быть в диапазоне от -3 до 5!");
                }
            } catch (NumberFormatException ex) {
                errors.put("x", "Координата X должна быть целым числом!");
            }
        }

        // Координата Y
        String yString = rawParams.get("y");
        if (yString == null || yString.isBlank()) {
            errors.put("y", "Введите значение Y");
        }
        else {
            try {
                y = Double.parseDouble(yString.trim().replace(",", "."));
                if (y < -5 || y > 3) {
                    errors.put("y", "Y должен быть в диапазоне от -5 до 3!");
                }
            } catch (NumberFormatException ex) {
                errors.put("y", "Координата Y должна быть вещественным числом!");
            }

        }

        // Параметр R
        String rString = rawParams.get("r");
        if (rString == null || rString.isBlank()) {
            errors.put("r", "Введите значение R!");
        }
        else {
            try {
                r = Integer.parseInt(rString.trim());
                if (r < 1 || r > 4) {
                    errors.put("r", "Значение R от 1 до 4!");
                }
            } catch (NumberFormatException ex) {
                errors.put("r", "R должно быть целым числом!");
            }
        }

        // Если поймали хотя бы одну ошибку - исключение
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return new ParsedData(x, y, r);
    }
}
