package commands;

import service.CollectionManager;
import transport.Request;
import model.Organization;
import java.util.Collections;
import java.util.LinkedList;

public class AddIfMinCommand extends AbstractCommand {
    public AddIfMinCommand(CollectionManager collectionManager) {
        super("add_if_min", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции", collectionManager);
    }

    @Override
    public boolean requiresObjectArgument() {
        return true;
    }

    @Override
    public void execute(Request request) {
        Organization org = request.getElem();

        if (collectionManager.getCollection().isEmpty()) {
            org.setId(collectionManager.generateNextId());
            collectionManager.add(org);
            System.out.println("Коллекция была пуста, элемент будет добавлен первым как минимальный!");
            return;
        }

        Organization minOrg = Collections.min(collectionManager.getCollection());

        if (org.compareTo(minOrg) < 0) {
            org.setId(collectionManager.generateNextId());
            collectionManager.add(org);
            System.out.println("Организация успешно добавлена!");
        }
        else {
            System.out.println("Организация не добавлена, так как она не меньше минимальной!");
        }
    }
}
