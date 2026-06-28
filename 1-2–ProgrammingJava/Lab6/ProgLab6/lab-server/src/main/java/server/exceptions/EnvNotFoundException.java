package server.exceptions;

import common.exceptions.BaseException;

/**
 * Исключение, которое выбрасывается при отсутствии переменной окружения
 * @author mikhailkramar
 */
public class EnvNotFoundException extends BaseException {
    public EnvNotFoundException() {
        super("Переменная окружения 'PATH_TO_XML' не найдена!");
    }
}
