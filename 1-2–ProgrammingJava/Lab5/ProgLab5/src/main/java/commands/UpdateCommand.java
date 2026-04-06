package commands;

import model.Organization;
import service.CollectionManager;
import transport.Request;
import utility.ArgumentType;

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
    public boolean canAcceptObject(String arg) {
        Long id = Long.parseLong(arg);
        return collectionManager.getById(id) != null;
    }

    @Override
    public void execute(Request request) {
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
            System.out.println("Организация с ID " + id + " не найдена(");
        }
        else {
            System.out.println("Организация с ID " + id + " успешно обновлена!");
        }
    }
}
