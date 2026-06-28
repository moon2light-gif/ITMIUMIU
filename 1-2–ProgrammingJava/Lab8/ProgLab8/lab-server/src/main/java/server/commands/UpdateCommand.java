package server.commands;

import common.model.Organization;
import common.transport.Response;
import server.database.OrganizationDatabaseManager;
import server.database.UserManager;
import server.service.CollectionManager;
import common.transport.Request;
import common.utility.ArgumentType;

/**
 * Класс команды "update", отвечающий за обновление элемента коллекции с введённым пользователем ID
 *
 * @author mikhailkramar
 */
public class UpdateCommand extends AbstractCommand {
    private final OrganizationDatabaseManager organizationDatabaseManager;
    private final UserManager userManager;

    public UpdateCommand(CollectionManager cm, OrganizationDatabaseManager organizationDatabaseManager, UserManager userManager) {
        super("update", "обновить значение элемента коллекции, id которого равен заданному", cm);
        this.organizationDatabaseManager = organizationDatabaseManager;
        this.userManager = userManager;
    }

    @Override
    public boolean requiresObjectArgument() {
        return true;
    }

    @Override
    public ArgumentType getArgumentType() {
        return ArgumentType.LONG;
    }

    @Override
    public boolean canAcceptArgument(String arg) {
        Long id = Long.parseLong(arg);
        //true, если организация с таким ID существует в коллекции
        return collectionManager.getById(id) != null;
    }

    @Override
    public boolean canAcceptThisId(String arg, String userLogin) {
        Long id = Long.parseLong(arg); //ID организации, над которой собираются произвести действие
        Organization organization = collectionManager.getById(id); //получаем организацию по ID из коллекции

        if (organization != null) {
            return java.util.Objects.equals(organization.getOwnerId(), userManager.getUserByLogin(userLogin)); //true, если владеет
        }
        return true; //метод возвращает по умолчанию true, если такого ID вообще нет в коллекции
    }

    @Override
    public Response execute(Request request) {
        String userLogin = request.getUserLogin(); //получаем логин пользователя
        Long id = Long.parseLong(request.getArgument()); //получаем ID организации, которую необходимо изменить
        Organization newOrg = request.getElem(); //получаем саму замену, которая будет вместо старой организации

        //работаем с БД
        boolean isUpdatedInDB = organizationDatabaseManager.update(userLogin, id, newOrg);

        if (isUpdatedInDB) {
            //в случае успешного обновления в БД, работаем с коллекцией в памяти
            Organization oldOrg = collectionManager.getById(id); //получаем старый элемент коллекции по его ID

            newOrg.setId(id); //КРИТИЧНО: сохраняем ID (клиент присылает объект без id) — иначе объект "пропадает" с карты
            if (oldOrg != null) {
                newOrg.setOwnerId(oldOrg.getOwnerId()); //выставляем тот же ID владельца
                newOrg.setCreationDate(oldOrg.getCreationDate()); //выставляем дату создания старого объекта

                int index = collectionManager.getCollection().indexOf(oldOrg); //индекс старого элемента
                if (index >= 0) {
                    collectionManager.getCollection().set(index, newOrg); //на его месте — новая организация
                } else {
                    collectionManager.getCollection().add(newOrg);
                }
            }

            return new Response(true, null, "Организация с ID " + id + " успешно обновлена!");
        }
        else {
            return new Response(false, null, "Ошибка при обновлении элемента: БД сейчас недоступна!");
        }

    }
}
