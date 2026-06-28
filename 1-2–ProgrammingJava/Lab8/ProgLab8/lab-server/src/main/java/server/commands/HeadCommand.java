package server.commands;

import common.model.Organization;
import common.transport.Request;
import common.transport.Response;
import server.service.CollectionManager;

/**
 * Класс {@code HeadCommand} описывает команду "head", которая отвечает за вывод первого элемента коллекции
 *
 * @author mikhailkramar
 */
public class HeadCommand extends AbstractCommand {
    public HeadCommand(CollectionManager cm) {
        super("head", "вывести первый элемент коллекции", cm);
    }
    //false и NONE

    @Override
    public Response execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            return new Response(true, collectionManager.getCollection(), "Коллекция сейчас пустует!");
        }

        //получаем первый элемент коллекции
        Organization org = collectionManager.getCollection().getFirst();
        return new Response(true, null, "Первый элемент коллекции:\n" + org);
    }
}
