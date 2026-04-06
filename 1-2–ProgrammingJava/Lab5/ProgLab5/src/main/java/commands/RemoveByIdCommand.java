package commands;

import service.CollectionManager;
import transport.Request;
import utility.ArgumentType;

public class RemoveByIdCommand extends AbstractCommand {
    public RemoveByIdCommand(CollectionManager cm) {
        super("remove_by_id", "удалить элемент из коллекции по его id", cm);
    }

    @Override
    public ArgumentType getArgumentType() {
        return ArgumentType.LONG;
    }

    @Override
    public void execute(Request request) {
        //ConsoleManager уже составил правильный request
        Long id = Long.parseLong(request.getArgument());
        boolean removed = collectionManager.getCollection().removeIf(org -> org.getId().equals(id));
        if (removed) {
            System.out.println("Организация с ID " + id + " успешно удалена!");
        } else {
            System.out.println("Организация с ID " + id + " не найдена!");
        }
    }
}
