package server.commands;

import common.transport.Request;
import common.transport.Response;
import server.database.UserManager;

import java.sql.SQLException;

/**
 * Класс {@code RegisterCommand} реализует команду "register", которая используется при регистрации нового пользователя
 *
 * @author mikhailkramar
 */
public class RegisterCommand extends AbstractCommand {
    public final UserManager userManager;

    public RegisterCommand(UserManager userManager) {
        super("register", "команда регистрации нового пользователя", null);
        this.userManager = userManager;
    }

    /**
     * Выполняет команду
     * @param request полученный от клиента запрос
     * @return ответ от сервера
     */
    @Override
    public Response execute(Request request) {
        try {
            boolean isRegistered = userManager.registerUser(request.getUserLogin(), request.getUserPassword());

            if (isRegistered) {
                Long ownerId = userManager.getUserByLogin(request.getUserLogin());
                java.util.Collection<Long> idHolder = (ownerId == null) ? null : java.util.List.of(ownerId);
                return new Response(true, idHolder, "\nРегистрация успешна! Добро пожаловать, " + request.getUserLogin() + "!");
            } else {
                return new Response(false, null, "Пользователь с таким логином уже существует!");
            }

        } catch (SQLException ex) {
            System.err.println("Ошибка во время регистрации: " + ex.getMessage());
            return new Response(false, null, "В данный момент регистрация недоступна! (нет связи с БД)");

        }
    }
}
