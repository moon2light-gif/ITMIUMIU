package server.commands;

import common.model.Organization;
import common.transport.Response;
import server.database.OrganizationDatabaseManager;
import server.service.CollectionManager;
import common.transport.Request;

import java.util.Collections;
import java.util.function.Predicate;

/**
 * Класс {@code AddIfMinCommand} описывает команду "add_if_min", которая добавляет элемент в коллекцию,
 * если его значение меньше наименьшего, существующего в коллекции, элемента
 *
 * @author mikhailkramar
 */
public class AddIfMinCommand extends AbstractCommand {
    private final OrganizationDatabaseManager organizationDatabaseManager;

    public AddIfMinCommand(CollectionManager collectionManager, OrganizationDatabaseManager organizationDatabaseManager) {
        super("add_if_min", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции", collectionManager);
        this.organizationDatabaseManager = organizationDatabaseManager;
    }

    @Override
    public boolean requiresObjectArgument() {
        return true;
    }

    /**
     * Выполняет команду
     * @param request запрос с данными для выполнения команды
     */
    @Override
    public Response execute(Request request) {
        Organization org = request.getElem();
        String userLogin = request.getUserLogin();

        if (collectionManager.getCollection().isEmpty()) {
            //работаем с БД
            long generatedId = organizationDatabaseManager.insert(userLogin, org);

            //добавляем в коллекцию
            org.setId(generatedId);
            collectionManager.add(org);
            return new Response(true, null,"Коллекция была пуста, элемент будет добавлен первым как минимальный!");
        }

        Organization minOrg = Collections.min(collectionManager.getCollection());

        if (org.compareTo(minOrg) < 0) {
            //работа с БД
            long generatedId = organizationDatabaseManager.insert(userLogin, org);

            //работа с памятью
            org.setId(generatedId);
            collectionManager.add(org);
            return new Response(true, null, "Организация успешно добавлена!");
        }
        else {
            return new Response(true, null, "Организация не добавлена, так как она не меньше минимальной!");
        }
    }
}
