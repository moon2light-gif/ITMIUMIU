package common.transport;
import common.model.Organization;

import java.io.Serializable;
import java.util.UUID;

/**
 * Класс {@code Request} представляет собой запрос, который содержит название команды, аргументы и экземпляр элемента коллекции.
 * Для лабораторной №7 данный класс дополнен логином и паролем пользователя, который отправляет текущий объект запроса.
 * Сформированный объект этого класса будет передаваться на сервер в качестве запроса
 *
 * @author mikhailkramar
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final String argument;
    private final Organization elem;

    private final String userLogin;
    private final String userPassword;

    //добавляю уникальный ID запроса
    private final UUID requestId = UUID.randomUUID();

    /**
     * Конструктор запроса
     * @param userLogin логин пользователя (имя в нашей системе)
     * @param userPassword пароль пользователя
     * @param commandName название команды
     * @param argument аргумент команды
     * @param elem объект организации
     */
    public Request(String userLogin, String userPassword, String commandName, String argument, Organization elem) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.commandName = commandName;
        this.argument = argument;
        this.elem = elem;
    }

    /**
     * Метод для получения из запроса имени команды, введённой пользователем
     * @return имя команды
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Метод для получения введённого пользователем аргумента команды
     * @return аргумент команды
     */
    public String getArgument() {
        return argument;
    }

    /**
     * Метод для получения отправленного объекта класса Organization
     * @return объект Organization
     */
    public Organization getElem() {
        return elem;
    }

    /**
     * Метод для получения логина пользователя, отправившего запрос
     * @return логин пользователя
     */
    public String getUserLogin() {
        return userLogin;
    }

    /**
     * Метод для получения пароля пользователя, который отправил запрос
     * @return пароль пользователя
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Метод для получения уникального ID запроса
     * @return ID запроса
     */
    public UUID getRequestId() {
        return requestId;
    }
}
