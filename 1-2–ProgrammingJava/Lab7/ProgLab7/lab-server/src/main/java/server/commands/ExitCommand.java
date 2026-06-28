package server.commands;

import common.transport.Response;
import server.service.CollectionManager;
import common.transport.Request;

public class ExitCommand extends AbstractCommand {
    public ExitCommand(CollectionManager cm) {
        super("exit", "завершить программу (без сохранения в файл)", cm);
    }

    @Override
    public Response execute(Request request) {
        System.exit(0);
        return new Response(true, null,"Завершение работы программы...\n");
    }
}
