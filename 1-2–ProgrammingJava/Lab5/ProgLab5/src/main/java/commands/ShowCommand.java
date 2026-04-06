package commands;

import model.Organization;
import service.CollectionManager;
import transport.Request;

public class ShowCommand extends AbstractCommand {
    public ShowCommand(CollectionManager cm) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении", cm);
    }

    //NONE и false

    @Override
    public void execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            System.out.println("Коллекция пуста, выводить нечего!");
            return;
        }
        System.out.println("Элементы коллекции:");
        for (Organization org : collectionManager.getCollection()) {
            System.out.println(org);
        }
    }
}
