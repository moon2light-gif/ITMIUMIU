package server.commands;

import common.transport.Request;
import common.transport.Response;
import server.database.OrganizationDatabaseManager;
import server.database.UserManager;
import server.service.CollectionManager;


/**
 * Класс {@code RemoveFirstCommand} описывает команду "remove_first", которая отвечает за удаление первого элемента коллекции
 *
 * @author mikhailkramar
 */
public class RemoveFirstCommand extends AbstractCommand {
    private final OrganizationDatabaseManager organizationDatabaseManager;

    public RemoveFirstCommand(CollectionManager cm, OrganizationDatabaseManager organizationDatabaseManager) {
        super("remove_first", "удалить первый элемент из коллекции", cm);
        this.organizationDatabaseManager = organizationDatabaseManager;
    }

    @Override
    public Response execute(Request request) {
        String userLogin = request.getUserLogin(); //получаем логин пользователя

        //если в коллекции вообще нет элементов
        if (collectionManager.getCollection().isEmpty()) {
            return new Response(false, null, "Коллекция пуста, удалять нечего!");
        }

        //получаем ID первого элемента коллекции (в памяти она отсортирована)
        Long orgId = collectionManager.getCollection().getFirst().getId();

        //работаем с БД
        boolean isRemovedFromDB = organizationDatabaseManager.removeById(userLogin, orgId);

        if (isRemovedFromDB) {
            //работаем с памятью (если успешно удалили из БД, то удаляем из памяти)
            collectionManager.getCollection().removeFirst();
            return new Response(true, null, "Организация с ID " + orgId + " успешно удалена!");
        } else {
            return new Response(false, null, "Ошибка: У вас нет прав на удаление первой организации!");
        }
    }
}
