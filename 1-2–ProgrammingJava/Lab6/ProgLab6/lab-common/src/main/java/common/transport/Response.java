package common.transport;

import common.utility.CommandDescriptor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Класс {@code Response} будет использоваться сервером для отправки ответа клиенту,
 * он содержит статус выполнения, изменённую коллекцию и текст сообщения, который должен увидеть пользователь
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

    public boolean isSuccess() {
        return this.success;
    }

    public Collection<?> getCollection() {
        return this.collection;
    }

    public String getMessage() {
        return this.message;
    }

    /**
     * Метод, который будет переопределён в классе наследнике
     * @return null (в контексте данного класса)
     */
    public Map<String, CommandDescriptor> getCommandDescriptorMap() {
        return null;
    }
}

