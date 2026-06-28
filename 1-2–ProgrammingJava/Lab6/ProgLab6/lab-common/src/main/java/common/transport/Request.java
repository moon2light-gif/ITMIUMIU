package common.transport;
import common.model.Organization;

import java.io.Serial;
import java.io.Serializable;

/**
 * Класс запроса, который содержит название команды, аргументы и экземпляр элемента коллекции.
 * Сформированный объект этого класса будет передаваться на сервер в качестве запроса
 *
 * @author mikhailkramar
 */
public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final String argument;
    private final Organization elem;

    /**
     * Конструктор запроса
     * @param commandName название команды
     * @param argument аргумент команды
     * @param elem объект организации
     */
    public Request(String commandName, String argument, Organization elem) {
        this.commandName = commandName;
        this.argument = argument;
        this.elem = elem;
    }

    /**
     * Метод для получения из запроса имени команды, введённой пользователем
     * @return имя команды
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Метод для получения введённого пользователем аргумента команды
     * @return аргумент команды
     */
    public String getArgument() {
        return argument;
    }

    /**
     * Метод для получения отправленного объекта класса Organization
     * @return объект Organization
     */
    public Organization getElem() {
        return elem;
    }

}
