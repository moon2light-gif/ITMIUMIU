package transport;
import model.Organization;

// собираем всё в один запрос, так удобнее обрабатывать, пригодится для следующей лабы
// commandName - update
// argument - id
// elem - объект Organization
public class Request {
    private final String commandName;
    private final String argument; //простые аргументы программы по типу id
    private final Organization elem; //экземпляр элемента, который мы будем вводить построчно

    public Request(String commandName, String argument, Organization elem) {
        this.commandName = commandName;
        this.argument = argument;
        this.elem = elem;
    }

    public String getCommandName() {
        return commandName;
    }
    public String getArgument() {
        return argument;
    }
    public Organization getElem() {
        return elem;
    }


}
