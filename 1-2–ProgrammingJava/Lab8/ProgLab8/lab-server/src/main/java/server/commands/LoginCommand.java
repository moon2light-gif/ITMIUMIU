package server.commands;

import common.transport.Request;
import common.transport.Response;
import server.database.UserManager;

import java.sql.SQLException;

/**
 * Класс {@code LoginCommand} реализует команду "login", которая используется при авторизации пользователя
 *
 * @author mikhailkramar
 */
public class LoginCommand extends AbstractCommand {
    private final UserManager userManager;

    public LoginCommand(UserManager userManager) {
        super("login", "команда входа пользователя", null);
        this.userManager = userManager;
    }

    /**
     * Выполняет команду по авторизации пользователя
     * @param request запрос от клиента
     * @return ответ от сервера
     */
    @Override
    public Response execute(Request request) {
        try {
            boolean isValid = userManager.authorizeUser(request.getUserLogin(), request.getUserPassword());

            if (isValid) {
                Long ownerId = userManager.getUserByLogin(request.getUserLogin());
                java.util.Collection<Long> idHolder = (ownerId == null) ? null : java.util.List.of(ownerId);
                return new Response(true, idHolder, "\nАвторизация успешна! Добро пожаловать, " + request.getUserLogin() + "!");
            }
            else {
                return new Response(false, null, "Неверный логин или пароль!");
            }

        } catch (SQLException ex) {
            System.err.println("Ошибка во время авторизации: " + ex.getMessage());
            return new Response(false, null, "В данный момент авторизация недоступна! (нет связи с БД)");
        }
    }
}
