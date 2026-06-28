package server.commands;

import common.transport.Response;
import server.service.CollectionManager;
import common.transport.Request;

/**
 * Класс команды "save", которая позволяет сохранить коллекцию в файл
 */
public class SaveCommand extends AbstractCommand {
    public SaveCommand(CollectionManager cm) {
        super("save", "сохранить коллекцию в файл (данная команда работает только на стороне сервера)", cm);
    }

    @Override
    public Response execute(Request request) {
        return new Response(true, null, collectionManager.save());
    }
}
