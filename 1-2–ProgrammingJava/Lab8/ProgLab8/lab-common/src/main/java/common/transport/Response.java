package common.transport;

import common.model.Organization;
import common.utility.CommandDescriptor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Класс {@code Response} будет использоваться сервером для отправки ответа клиенту,
 * он содержит статус выполнения, необходимую коллекцию и текст сообщения, который должен увидеть пользователь
 *
 * @author mikhailkramar
 */
public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String message;
    private final Collection<?> collection;

    /**
     * Конструктор ответа от сервера
     * @param success флаг выполнения запроса (успешно/ошибка)
     * @param collection коллекция, которая должна быть возвращена той или иной командой
     * @param message сообщение после выполнения команды
     */
    public Response(boolean success, Collection<?> collection, String message) {
        this.success = success;
        this.collection = collection;
        this.message = message;
    }

    public Collection<?> getCollection() {
        return collection;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, CommandDescriptor> getCommandDescriptorMap() {
        return null;
    }
}
