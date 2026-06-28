package server.commands;

import common.model.Organization;
import common.transport.Request;
import common.transport.Response;
import server.database.OrganizationDatabaseManager;
import server.database.UserManager;
import server.service.CollectionManager;

import java.sql.SQLException;

/**
 * Класс {@code AddCommand} описывает команду "add", которая позволяет добавлять элементы Organization в коллекцию
 *
 * @author mikhailkramar
 */
public class AddCommand extends AbstractCommand {
    private final OrganizationDatabaseManager organizationDatabaseManager;

    public AddCommand(CollectionManager collectionManager, OrganizationDatabaseManager organizationDatabaseManager) {
        super("add", "добавить новый элемент в коллекцию", collectionManager);
        this.organizationDatabaseManager = organizationDatabaseManager;
    }

    //add требует объект организации
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
        Organization org = request.getElem(); //элемент организации
        String userLogin = request.getUserLogin(); //логин пользователя

        //сначала добавляем в БД
        long generatedId = organizationDatabaseManager.insert(userLogin, org);

        if (generatedId == -1L) {
            return new Response(false, null, "Ошибка при добавлении элемента: БД сейчас недоступна!");
        }

        //добавление в память
        org.setId(generatedId);
        collectionManager.add(org);

        return new Response(true, null, "Организация успешно добавлена!");
    }

}
