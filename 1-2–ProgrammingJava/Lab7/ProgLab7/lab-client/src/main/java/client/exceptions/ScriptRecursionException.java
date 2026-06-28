package client.exceptions;

import common.exceptions.BaseException;

/**
 * Исключение, возникающее при обнаружении бесконечной рекурсии в скрипте
 * @author mikhailkramar
 */
public class ScriptRecursionException extends BaseException {
    public ScriptRecursionException() {
        super("Обнаружена бесконечная рекурсия!");
    }
}
