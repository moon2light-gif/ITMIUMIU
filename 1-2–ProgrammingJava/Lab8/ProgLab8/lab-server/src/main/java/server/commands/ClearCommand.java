package server.commands;

import common.transport.Response;
import server.database.OrganizationDatabaseManager;
import server.database.UserManager;
import server.service.CollectionManager;
import common.transport.Request;

/**
 * Класс {@code ClearCommand} описывает команду "clear", которая позволяет очистить коллекцию
 *
 * @author mikhailkramar
 */
public class ClearCommand extends AbstractCommand {
    private final OrganizationDatabaseManager organizationDatabaseManager;
    private final UserManager userManager;

    public ClearCommand(CollectionManager collectionManager, OrganizationDatabaseManager organizationDatabaseManager, UserManager userManager) {
        super("clear", "очистить коллекцию", collectionManager);
        this.organizationDatabaseManager = organizationDatabaseManager;
        this.userManager = userManager;
    }

    @Override
    public Response execute(Request request) {

        String userLogin = request.getUserLogin(); //получаем логин пользователя
        Long ownerId = userManager.getUserByLogin(userLogin);

        if (collectionManager.isUserCollectionEmpty(ownerId)) {
            return new Response(true, null, "Коллекция из ваших элементов и так пустует!");
        }

        //работа с БД
        boolean isDeleted = organizationDatabaseManager.clearByUser(userLogin);

        if (isDeleted) {
            //работа с памятью
            collectionManager.clearByUser(ownerId);
            return new Response(true, null, "Элементы коллекции данного пользователя успешно удалены!");
        }
        return new Response(false,null, "Ошибка SQL: Элементы данного пользователя не могут быть удалены!");
    }
}
