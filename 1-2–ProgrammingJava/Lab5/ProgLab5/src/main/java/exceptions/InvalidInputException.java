package exceptions;

//выбрасывается при неправильном вводе пользователя или при том самом ctrl+d
public class InvalidInputException extends BaseException {
    public InvalidInputException(String message) {
        super("Ошибка ввода: " + message);
    }
}
