package server.commands;

import common.transport.Response;
import server.service.CollectionManager;
import common.transport.Request;


/**
 * Класс {@code RemoveFirstCommand} описывает команду "remove_first", которая отвечает за удаление первого элемента коллекции
 *
 * @author mikhailkramar
 */
public class RemoveFirstCommand extends AbstractCommand {
    public RemoveFirstCommand(CollectionManager cm) {
        super("remove_first", "удалить первый элемент из коллекции", cm);
    }

    @Override
    public Response execute(Request request) {

        if (!collectionManager.getCollection().isEmpty()) {
            collectionManager.getCollection().removeFirst();
            return new Response(true, null, "Первый элемент коллекции успешно удалён!");
        }

        else {
            return new Response(true, null, "Коллекция пуста, удалять нечего!");
        }
    }
}
