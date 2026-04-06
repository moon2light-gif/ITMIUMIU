package commands;

import transport.Request;
import service.CollectionManager;

public class ClearCommand extends AbstractCommand {
    public ClearCommand(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию", collectionManager);
    }

    @Override
    public void execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            System.out.println("Коллекция и так пустует!");
            return;
        }
        collectionManager.clear();
        System.out.println("Коллекция успешно очищена!");
    }
}
