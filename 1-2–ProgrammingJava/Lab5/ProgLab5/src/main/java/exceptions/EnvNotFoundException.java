package exceptions;

public class EnvNotFoundException extends BaseException {
    public EnvNotFoundException() {
        super("Переменная окружения 'PATH_TO_XML' не найдена!");
    }
}
