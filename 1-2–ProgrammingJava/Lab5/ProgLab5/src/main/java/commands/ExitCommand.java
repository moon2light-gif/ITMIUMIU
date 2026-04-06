package commands;

import service.CollectionManager;
import transport.Request;

public class ExitCommand extends AbstractCommand {
    public ExitCommand(CollectionManager cm) {
        super("exit", "завершить программу (без сохранения в файл)", cm);
    }
    //false и NONE

    @Override
    public void execute(Request request) {
        System.out.println("Завершение работы программы...");
        System.exit(0); //??
    }
}
