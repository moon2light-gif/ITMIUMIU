package common.exceptions;


/**
 * Исключение, выбрасываемое при неправильном вводе пользователя
 * @author mikhailkramar
 */
public class InvalidInputException extends BaseException {
    public InvalidInputException(String message) {
        super("Ошибка ввода: " + message);
    }
}
