package server.commands;

import common.model.Organization;
import common.transport.Response;
import server.service.CollectionManager;
import common.transport.Request;
import common.utility.ArgumentType;

/**
 * Класс команды "update", отвечающий за обновление элемента коллекции с введённым пользователем ID
 *
 * @author mikhailkramar
 */
public class UpdateCommand extends AbstractCommand {
    public UpdateCommand(CollectionManager cm) {
        super("update", "обновить значение элемента коллекции, id которого равен заданному", cm);
    }

    @Override
    public boolean requiresObjectArgument() {
        return true;
    }

    @Override
    public ArgumentType getArgumentType() {
        return ArgumentType.LONG;
    }

    @Override
    public boolean canAcceptArgument(String arg) {
        Long id = Long.parseLong(arg);
        return collectionManager.getById(id) != null;
    }

    @Override
    public Response execute(Request request) {

        Long id = Long.parseLong(request.getArgument());
        Organization elem = request.getElem();

        boolean found = false;

        for (int i = 0; i < collectionManager.getCollection().size(); i++) {
            //находим элемент с id равным заданному
            if (collectionManager.getCollection().get(i).getId().equals(id)) {
                elem.setId(id);
                collectionManager.getCollection().set(i, elem);
                found = true;
                break;
            }
        }

        if (!found) {
            return new Response(true, null, "Организация с ID " + id + " не найдена(");
        }
        else {
            return new Response(true, null, "Организация с ID " + id + " успешно обновлена!");
        }
    }
}
