package exceptions;

//специально под выполнение скрипта, если будет обнаружена бесконечная рекурсия
public class ScriptRecursionException extends BaseException {
    public ScriptRecursionException() {
        super("Обнаружена бесконечная рекурсия!");
    }
}
