package common.exceptions;

/**
 * Базовое исключение, от которого наследуются все остальные
 * @author mikhailkramar
 */
public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }
}
