package server.commands;

import common.transport.Request;
import common.transport.Response;
import common.utility.ArgumentType;
import common.utility.CommandDescriptor;

/**
 * Интерфейс {@code Command} предоставляет все необходимые методы, которые должна реализовывать каждая команда
 *
 * @author mikhailkramar
 */
public interface Command {
    Response execute(Request request); //этот метод возвращает ответ для клиента
    String getName();
    String getDescription();

    boolean canAcceptArgument(String arg); //метод для проверки возможности получения такого аргумента командой
    boolean canAcceptThisId(String arg, String userLogin); //метод для возможности работы с полученным ID организации

    ArgumentType getArgumentType(); //аргументы команды
    boolean requiresObjectArgument(); //нужен ли команде сложный объект в качестве аргумента
    CommandDescriptor getDescriptor(); //получаем "паспорт" для каждой команды
}
