package server.service;

import common.transport.Request;
import common.transport.Response;
import server.commands.Command;

/**
 * Класс {@code RequestExecutor} отвечает за исполнение запроса, полученного сервером от клиента
 *
 * @author mikhailkramar
 */
public class RequestExecutor {
    private final CommandManager commandManager;
    private final CollectionManager collectionManager;

    /**
     * Конструктор исполнителя запросов
     * @param commandManager менеджер команд
     */
    public RequestExecutor(CommandManager commandManager, CollectionManager collectionManager) {
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
    }

    /**
     * Метод для исполнения запроса, вызывающий исполнение конкретной команды
     * @param request поступивший от клиента запрос
     * @return объект класса Response - ответа от сервера
     */
    public Response execute(Request request) {

        if (request == null) {
            return new Response(false,null, "Получен пустой запрос.");
        }

        if ("check_name".equals(request.getCommandName())) {
            boolean isUnique = collectionManager.fullNameIsUnique(request.getArgument());
            return new Response(isUnique, null, isUnique ? "Имя свободно" : "Имя занято");
        }

        Command command = commandManager.getCommand(request.getCommandName());

        if (command == null) {
            return new Response(false, null, "Команда не найдена.");
        }

        //проверка на корректность ввёденного ID (для команды update)
        if (command.requiresObjectArgument() && request.getElem() == null) {
            boolean validArg = command.canAcceptArgument(request.getArgument());
            boolean validId = command.canAcceptThisId(request.getArgument(), request.getUserLogin());

            //если оба условия выполнены, то можно продолжать ввод организации
            if (validArg && validId) {
                return new Response(true, null, "");
            }

            if (!validArg) {
                return new Response(false, null, "Условия команды " + command.getName() + " не соблюдены: ID не найден!");

            }
            return new Response(false, null, "Условия команды " + command.getName() + " не соблюдены: Организация с данным ID вам не принадлежит!");

        }

        return command.execute(request); //возвращаем собранный Response, который получаем от команды
    }
}
