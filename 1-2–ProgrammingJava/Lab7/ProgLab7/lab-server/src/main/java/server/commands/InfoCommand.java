package server.commands;

import common.transport.Request;
import common.transport.Response;
import server.service.CollectionManager;

/**
 * Класс {@code InfoCommand} описывает команды "info", которая отвечает за получение информации о коллекции
 *
 * @author mikhailkramar
 */
public class InfoCommand extends AbstractCommand {

    public InfoCommand(CollectionManager cm) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции", cm);
    }

    @Override
    public Response execute(Request request) {
        return new Response(true, null, collectionManager.getInfo());
    }

}
