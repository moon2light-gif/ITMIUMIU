package server.commands;

import common.transport.Response;
import server.service.CollectionManager;
import common.transport.Request;

/**
 * Класс {@code ClearCommand} описывает команду "clear", которая позволяет очистить коллекцию
 *
 * @author mikhailkramar
 */
public class ClearCommand extends AbstractCommand {

    public ClearCommand(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию", collectionManager);
    }

    @Override
    public Response execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            return new Response(true,null, "Коллекция и так пустует!");
        }

        collectionManager.clear();

        return new Response(true, null, "Коллекция успешно очищена!");
    }
}
