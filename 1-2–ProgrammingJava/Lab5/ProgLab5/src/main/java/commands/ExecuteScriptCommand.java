package commands;

import exceptions.ScriptRecursionException;
import service.CollectionManager;
import transport.Request;
import utility.ArgumentType;
import utility.ConsoleManager;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.HashSet;

public class ExecuteScriptCommand extends AbstractCommand {
    //стек для хранения путей файлов, которые выполняются на данный момент
    private static final Deque<String> pathStack = new ArrayDeque<>();
    private ConsoleManager consoleManager;

    public ExecuteScriptCommand(CollectionManager cm) {
        super("execute_script", "считать и исполнить скрипт из указанного файла", cm);
    }

    //установим связь с ConsoleManager
    public void setConsoleManager(ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
    }

    @Override
    public ArgumentType getArgumentType() {
        return ArgumentType.STRING;
    }

    @Override
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
