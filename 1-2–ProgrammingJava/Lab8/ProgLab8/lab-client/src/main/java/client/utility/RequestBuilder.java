package client.utility;

import client.connection.Client;
import common.model.Coordinates;
import common.model.Organization;
import common.transport.Request;
import common.transport.Response;
import common.utility.ArgumentType;
import common.utility.CommandDescriptor;

import java.util.Map;

/**
 * Класс {@code RequestBuilder} собирает полностью корректный запрос, содержащий имя команды, аргумент и элемент организации
 *
 * @author mikhailkramar
 */
public class RequestBuilder {
    private final ElementReader elementReader;
    private Map<String, CommandDescriptor> commands;

    private Client udpClient; //нужен для отправки проверочного запроса

    private String userLogin;
    private String userPassword;

    /**
     * Конструктор сборщика корректного запроса
     * @param elementReader объект класса, предназначенного для чтения объекта типа Organization
     * @param commands мапа, содержащая в качестве ключа имя команды и ставящая ей в соответствие её описание
     */
    public RequestBuilder(ElementReader elementReader, Map<String, CommandDescriptor> commands) {
        this.elementReader = elementReader;
        this.commands = commands;
    }

    /**
     * Метод для установки зависимости от UDPClient, который вызывается в Client
     * @param udpClient объект класса UDPClient
     */
    public void setUdpClient(Client udpClient) {
        this.udpClient = udpClient;
    }

    /**
     * Метод для установки логина и пароля пользователя конкретному объекту RequestBuilder
     * @param userLogin логин
     * @param userPassword пароль
     */
    public void setUserData(String userLogin, String userPassword) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
    }

    /**
     * Метод для установки дескриптора команд requestBuilder'y
     * @param commands дескриптор команд
     */
    public void setCommands(Map<String, CommandDescriptor> commands) {
        this.commands = commands;
    }

    /**
     * Метод для сборки запроса
     * @param line вся введённая строка
     * @return Объект собранного запроса или null
     */
    public Request buildRequest(String line, boolean isFileMode) {
        String[] parts = line.split("\\s+", 2);
        String name = parts[0].toLowerCase(); //выделили имя команды
        String arg = parts.length > 1 ? parts[1] : "";

        CommandDescriptor commandDesc = commands.get(name); //получаем описание команды по её имени
        //валидация имени команды при помощи CommandDescriptor
        if (commandDesc == null) {
            System.out.println("Ошибка: Такая команда не найдена!");
            return null;
        }

        //валидация "формата" обычного аргумента/аргументов
        if (!validateArgsFormat(commandDesc, arg)) {
            return null;
        }

        Organization org = null;

        //собираем Organization, только после всех пройденных проверок
        if (commandDesc.isRequiresObjectArgument()) {

            Response checkResponse = validateArgsOnServer(name, arg);

            if (checkResponse == null) {
                if (isFileMode) throw new RuntimeException("Прерывание: сервер не отвечает!");
                return null;
            }

            if (!checkResponse.isSuccess()) {
                System.out.println(checkResponse.getMessage());
                if (isFileMode) {
                    throw new RuntimeException("Прерывание из-за не соблюдения условий выполнения команды!");
                }
                return null;
            }

            org = new Organization(
                    elementReader.askName(),
                    new Coordinates(elementReader.askX(), elementReader.askY()),
                    elementReader.askAnnualTurnover(),
                    elementReader.askFullName(),
                    elementReader.askType(),
                    elementReader.askOfficialAddress()
            );

        }

        //Готовый объект запроса
        return new Request(userLogin, userPassword, name, arg, org);

    }

    /**
     * Метод для проверки аргументов команды на сервере
     * @param name имя команды
     * @param arg введённый аргумент команды
     * @return объект ответа от сервера
     */
    protected Response validateArgsOnServer(String name, String arg) {
        //отсылаем проверочный запрос, чтобы проверить только аргумент
        Request checkRequest = new Request(userLogin, userPassword, name, arg, null);
        return udpClient.sendAndReceive(checkRequest);
    }

    private boolean validateArgsFormat(CommandDescriptor commandDesc, String arg) {
        ArgumentType type = commandDesc.getArgumentType(); //получаем тип аргумента у обрабатываемой команды
        //если у команды нет аргумента, но пользователь зачем-то ввёл
        if (type == ArgumentType.NONE && !arg.isEmpty()) {
            System.out.println("Ошибка: Команда " + commandDesc.getName() + " не принимает аргументы!");
            return false;
        }
        //если у команды есть аргумент, но пользователь ничего не ввёл
        if (type != ArgumentType.NONE && arg.isEmpty()) {
            System.out.println("Ошибка: Команда " + commandDesc.getName() + " требует аргумент!");
            return false;
        }
        //если в строке после аргумента есть пробелы, значит, ввели больше одного аргумента (кстати, корректно отработает, если кто-то случайно ввёл update 12 и пробел после, так как до этого прописан trim())
        if (!arg.isEmpty() && arg.split("\\s+").length > 1) {
            System.out.println("Ошибка: Команда " + commandDesc.getName() + " принимает только один обычный аргумент!");
            return false;
        }
        //валидация ввёденных чисел
        try {
            if (type == ArgumentType.LONG) {
                Long.parseLong(arg);
            }
            if (type == ArgumentType.INT) {
                Integer.parseInt(arg);
            }
        } catch (NumberFormatException ex) {
            System.out.println("Ошибка: Аргумент должен быть числом типа " + type +"!");
            return false;
        }

        //если валидация успешно пройдена
        return true;
    }
}
