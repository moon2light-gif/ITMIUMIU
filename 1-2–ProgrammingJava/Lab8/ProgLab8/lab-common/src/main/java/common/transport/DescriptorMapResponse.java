package common.transport;

import common.utility.CommandDescriptor;

import java.util.Collection;
import java.util.Map;

/**
 * Класс {@code DescriptorMapResponse} отвечает за ответ от сервера, который возвращает дескриптор команд, доступных клиенту
 *
 * @author mikhailkramar
 */
public class DescriptorMapResponse extends Response {
    private final Map<String, CommandDescriptor> commandDescriptorMap;

    public DescriptorMapResponse(boolean success, String message, Map<String, CommandDescriptor> commandDescriptorMap) {
        super(success, null, message);
        this.commandDescriptorMap = commandDescriptorMap;
    }

    /**
     * Метод, возвращающий map из имени команды и дескриптора для неё
     * @return дескриптор команд
     */
    @Override
    public Map<String, CommandDescriptor> getCommandDescriptorMap() {
        return commandDescriptorMap;
    }
}
