package client.utility;

import client.commands.ScriptExecutor;
import client.connection.Client;
import client.exceptions.ServerUnavailableException;
import common.transport.Request;
import common.transport.Response;
import common.utility.ArgumentType;
import common.utility.CommandDescriptor;

import javax.security.sasl.AuthorizeCallback;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

/** Класс {@code ConsoleManager} отвечает за консоль клиента,
 * за переключение режимов между чтением из файла и вводом напрямую
 *
 * @author mikhailkramar
 */
public class ConsoleManager {
    private final ElementReader elementReader;
    private final Scanner scanner;
    private final RequestBuilder requestBuilder;
    private final ScriptExecutor scriptExecutor;
    private final Client udpClient;
    private final AuthManager authManager;

    public ConsoleManager(ElementReader er, Scanner sc, RequestBuilder requestBuilder, ScriptExecutor scriptExecutor, Client udpClient, AuthManager authManager) {
        this.elementReader = er;
        this.scanner = sc;
        this.requestBuilder = requestBuilder;
        this.scriptExecutor = scriptExecutor;
        this.udpClient = udpClient;
        this.authManager = authManager;
    }

    /**
     * Метод, предназначенный для обновления данных сессии при входе в учётную запись
     */
    public void updateSessionData() {
        requestBuilder.setUserData(authManager.getUserLogin(), authManager.getUserPassword());

        //запрос на получение доступных пользователю команд
        Request startRequest = new Request(authManager.getUserLogin(), authManager.getUserPassword(), "return_map", null, null);

        System.out.println("Загрузка доступных команд с сервера...");

        //Получаем доступные команды, которые содержатся в startResponse
        Response startResponse = udpClient.sendAndReceive(startRequest);

        if (startResponse == null || !startResponse.isSuccess()) {
            System.err.println("Критическая ошибка! Не получилось получить доступные команды от сервера!");
            System.exit(0);
        }

        //Инициализируем map из названий всех доступных команд и их подробных описаний
        Map<String, CommandDescriptor> commandDescriptorMap = startResponse.getCommandDescriptorMap();

        //добавляем команды доступные только клиенту
        commandDescriptorMap.put("exit", new CommandDescriptor("exit", ArgumentType.NONE, false));
        commandDescriptorMap.put("execute_script", new CommandDescriptor("execute_script", ArgumentType.STRING, false));
        commandDescriptorMap.put("logout", new CommandDescriptor("logout", ArgumentType.NONE, false));

        requestBuilder.setCommands(commandDescriptorMap);
    }

    /**
     * Метод, который вызывается при запуске приложения
     */
    public void start() {
        updateSessionData();

        System.out.println("Введите 'help' для просмотра доступных команд.");
        processInput(this.scanner, false);
    }

    /**
     * Метод, предназначенный для смены работы приложения на файловый мод
     * @param fileName имя скрипта
     */
    public void fileMode(String fileName) {
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            processInput(fileScanner, true);
            System.out.println("Скрипт " + fileName + " успешно выполнен!");
        } catch (Exception ex) {
            System.out.println("Ошибка скрипта: " + ex.getMessage());
        }
    }

    /**
     * Метод, отвечающий за процесс ввода на стороне клиента, который может быть как из консоли, так и из файла
     * @param currentScanner текущий Scanner
     * @param isFileMode флаг включен/выключен файловый режим ввода
     */
    private void processInput(Scanner currentScanner, boolean isFileMode) {
        elementReader.setInteractive(!isFileMode);
        elementReader.setScanner(currentScanner);

        while (currentScanner.hasNextLine()) {
            //читаем введённую строчку
            String line = currentScanner.nextLine().trim();

            if (line.isEmpty()) continue; //пусть вводит, пока не ввёл

            try {
                //собираем сам запрос
                Request request = requestBuilder.buildRequest(line, isFileMode);

                //если запрос не собран (введены некорректные данные), продолжаем читать ввод
                if (request == null) {
                    continue;
                }

                if (request.getCommandName().equals("exit")) {
                    System.out.println("Завершение работы клиента.");
                    break;
                }

                if (request.getCommandName().equals("execute_script")) {
                    scriptExecutor.execute(request);
                }

                if (request.getCommandName().equals("logout")) {
                    System.out.println("Вы успешно вышли из учётной записи.\n");

                    if (isFileMode) break;

                    //запускаем меню авторизации
                    authManager.runAuthorizationMenu();

                    //обновляем данные сессии (логин, пароль, возможно команды)
                    updateSessionData();

                    System.out.println("Введите 'help' для просмотра доступных команд.");
                    continue;
                }

                else {

                    //отправляем объект запроса на сервер и получаем response
                    Response response = udpClient.sendAndReceive(request);

                    if (response != null) {
                        //выводим сообщение от команды
                        System.out.println(response.getMessage());

                        Collection<?> collection = response.getCollection();
                        //выводим коллекцию, если команда её вернула
                        if (collection != null) {
                            for (var elem : collection) {
                                System.out.println(elem);
                            }
                        }

                    }
                }

            } catch (ServerUnavailableException ex) {
                System.err.println("Ошибка подключения: " + ex.getMessage());
                if (isFileMode) break;

            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("Ошибка выполнения: " + ex.getMessage());
                if (isFileMode) break;

            } catch (RuntimeException ex) {
                System.err.println("Произошла внутренняя ошибка клиента!");

                if (isFileMode) {
                    System.err.println("Выполнение скрипта прервано из-за ошибки в коде!");
                    break;
                }
            }
        }

        elementReader.setScanner(this.scanner);
        elementReader.setInteractive(true);

        if (isFileMode) {
            System.out.println("\nПоток ввода завершён! Завершение работы...");
            System.exit(0);
        }
    }
}