package server.commands;

import common.transport.Response;
import server.service.CollectionManager;
import common.transport.Request;
import common.utility.ArgumentType;

/**
 * Класс команды "remove_by_id", отвечающий за удаление элемента из коллекции по ID
 *
 * @author mikhailkramar
 */
public class RemoveByIdCommand extends AbstractCommand {
    public RemoveByIdCommand(CollectionManager cm) {
        super("remove_by_id", "удалить элемент из коллекции по его id", cm);
    }

    @Override
    public ArgumentType getArgumentType() {
        return ArgumentType.LONG;
    }

    @Override
    public Response execute(Request request) {

        Long id = Long.parseLong(request.getArgument());

        boolean removed = collectionManager.getCollection().removeIf(org -> org.getId().equals(id));

        if (removed) {
            return new Response(true,  null, "Организация с ID " + id + " успешно удалена!");
        }
        else {
            return new Response(true, null, "Организация с ID " + id + " не найдена!");
        }
    }
}
