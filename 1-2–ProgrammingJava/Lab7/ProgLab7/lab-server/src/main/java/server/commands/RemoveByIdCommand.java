package server.commands;

import common.model.Organization;
import common.transport.Request;
import common.transport.Response;
import common.utility.ArgumentType;
import server.database.OrganizationDatabaseManager;
import server.database.UserManager;
import server.service.CollectionManager;

/**
 * Класс команды "remove_by_id", отвечающий за удаление элемента из коллекции по ID
 *
 * @author mikhailkramar
 */
public class RemoveByIdCommand extends AbstractCommand {
    private final OrganizationDatabaseManager organizationDatabaseManager;
    private final UserManager userManager;

    public RemoveByIdCommand(CollectionManager cm, OrganizationDatabaseManager organizationDatabaseManager, UserManager userManager) {
        super("remove_by_id", "удалить элемент из коллекции по его id", cm);
        this.organizationDatabaseManager = organizationDatabaseManager;
        this.userManager = userManager;
    }

    @Override
    public ArgumentType getArgumentType() {
        return ArgumentType.LONG;
    }


    @Override
    public boolean canAcceptThisId(String arg, String userLogin) {
        Long id = Long.parseLong(arg); //ID организации, над которой собираются произвести действие
        Organization organization = collectionManager.getById(id); //получаем организацию по ID из коллекции

        if (organization != null) {
            return collectionManager.getById(id).getOwnerId().equals(userManager.getUserByLogin(userLogin)); //true, если владеет
        }
        return true; //метод возвращает по умолчанию true, если такого ID вообще нет в коллекции
    }

    @Override
    public Response execute(Request request) {
        String userLogin = request.getUserLogin(); //получаем логин пользователя
        Long orgId = Long.parseLong(request.getArgument()); //получаем ID организации, которую необходимо удалить

        //работаем с БД (пытаемся удалить организацию с таким ID из БД)
        boolean isRemovedFromDB = organizationDatabaseManager.removeById(userLogin, orgId);
        // ^ возможны два случая: 1) этот ID вообще не существует; 2) этот ID не принадлежит пользователю

        if (isRemovedFromDB) {
            //работаем с памятью (если успешно удалили из БД, то удаляем из памяти)
            collectionManager.getCollection().removeIf((org) -> org.getId().equals(orgId));
            return new Response(true, null, "Организация с ID " + orgId + " успешно удалена!");
        }
        //если isRemovedFromDB == false && организация с таким ID не принадлежит пользователю
        if (!canAcceptThisId(request.getArgument(), userLogin)) {
            return new Response(false, null, "Ошибка: У вас нет прав на удаление этой организации!");
        }

        return new Response(false, null, "Ошибка: Удаление невозможно, так как организации с таким ID не существует!");
    }
}
