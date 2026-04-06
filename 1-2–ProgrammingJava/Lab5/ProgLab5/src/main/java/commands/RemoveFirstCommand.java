package commands;

import service.CollectionManager;
import transport.Request;

public class RemoveFirstCommand extends AbstractCommand {
    public RemoveFirstCommand(CollectionManager cm) {
        super("remove_first", "удалить первый элемент из коллекции", cm);
    }

    //NONE и false

    @Override
    public void execute(Request request) {
        if (!collectionManager.getCollection().isEmpty()) {
            collectionManager.getCollection().removeFirst();
            System.out.println("Первый элемент коллекции успешно удалён!");
        }
        else {
            System.out.println("Коллекция пуста, удалять нечего.");
        }
    }
}
