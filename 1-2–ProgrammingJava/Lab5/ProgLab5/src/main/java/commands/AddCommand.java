package commands;

import model.Organization;
import service.CollectionManager;
import transport.Request;
import utility.ArgumentType;

public class AddCommand extends AbstractCommand {
    public AddCommand(CollectionManager collectionManager) {
        super("add", "добавить новый элемент в коллекцию", collectionManager);
    }

    //add требует объект организации
    @Override
    public boolean requiresObjectArgument() {
        return true; //в ConsoleManager запустится ElementReader
    }

    //не пишем getArgumentType, так как по умолчанию и так NONE

    @Override
    public void execute(Request request) {
        Organization org = request.getElem();
        org.setId(collectionManager.generateNextId());
        collectionManager.add(request.getElem());
        System.out.println("Организация успешно добавлена!");
    }

}
