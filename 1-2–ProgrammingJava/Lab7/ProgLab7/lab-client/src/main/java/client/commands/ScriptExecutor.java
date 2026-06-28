package client.commands;

import client.exceptions.ScriptRecursionException;
import client.utility.ConsoleManager;
import common.transport.Request;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Класс {@code ScriptExecutor} отвечает за переключение на чтение файла
 * при вводе команды execute_script
 *
 * @author mikhailkramar
 */
public class ScriptExecutor {
    private ConsoleManager consoleManager;

    //стек для хранения путей файлов, которые выполняются на данный момент
    private static final Deque<String> pathStack = new ArrayDeque<>();

    public void setConsoleManager(ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
    }

    public void execute(Request request) {
        String path = request.getArgument();
        File file = new File(path);

        //получаем абсолютный путь (а то можно будет обмануть рекурсию через .../test.txt)
        String absolutePath = file.getAbsolutePath();

        if (pathStack.contains(absolutePath)) {
            throw new ScriptRecursionException();
        }

        pathStack.push(absolutePath);

        try {
            //эта команда по факту говорит consoleManager начинать читать всё из этого файла
            consoleManager.fileMode(path); //fileMode запускает тот же цикл, что и .start()
        } finally {
            //после завершения удалим файл из стека
            pathStack.pop();
        }
    }
}
