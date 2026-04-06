package commands;

import model.Organization;
import service.CollectionManager;
import transport.Request;


public class HeadCommand extends AbstractCommand {
    public HeadCommand(CollectionManager cm) {
        super("head", "вывести первый элемент коллекции", cm);
    }
    //false и NONE

    @Override
    public void execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            System.out.println("Коллекция сейчас пустует!");
            return;
        }
        //получаем первый элемент коллекции
        Organization org = collectionManager.getCollection().getFirst();
        System.out.println("Первый элемент коллекции:\n" + org);
    }
}
