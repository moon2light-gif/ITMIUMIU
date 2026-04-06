package commands;

import service.CollectionManager;
import transport.Request;

public class InfoCommand extends AbstractCommand {
    public InfoCommand(CollectionManager cm) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции", cm);
    }

    //NONE и false

    @Override
    public void execute(Request request) {
        System.out.println(collectionManager.getInfo());
    }

}
