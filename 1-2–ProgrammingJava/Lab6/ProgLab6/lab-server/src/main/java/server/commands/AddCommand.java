package server.commands;

import common.model.Organization;
import common.transport.Request;
import common.transport.Response;
import server.service.CollectionManager;

/**
 * Класс {@code AddCommand} описывает команду "add", которая позволяет добавлять элементы Organization в коллекцию
 *
 * @author mikhailkramar
 */
public class AddCommand extends AbstractCommand {

    public AddCommand(CollectionManager collectionManager) {
        super("add", "добавить новый элемент в коллекцию", collectionManager);
    }

    //add требует объект организации
    @Override
    public boolean requiresObjectArgument() {
        return true;
    }

    /**
     * Выполняет команду
     * @param request запрос с данными для выполнения команды
     */
    @Override
    public Response execute(Request request) {
        Organization org = request.getElem();
        org.setId(collectionManager.generateNextId());
        collectionManager.add(request.getElem());
        return new Response(true, null, "Организация успешно добавлена!");
    }

}
