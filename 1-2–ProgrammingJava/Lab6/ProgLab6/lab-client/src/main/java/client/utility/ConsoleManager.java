package client.utility;

import client.commands.ScriptExecutor;
import client.connection.Client;
import client.exceptions.ServerUnavailableException;
import common.model.Organization;
import common.transport.Request;
import common.transport.Response;
import common.utility.CommandDescriptor;

import java.awt.*;
import java.io.File;
import java.util.*;

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

    public ConsoleManager(ElementReader er, Scanner sc, RequestBuilder requestBuilder, ScriptExecutor scriptExecutor, Client udpClient) {
        this.elementReader = er;
        this.scanner = sc;
        this.requestBuilder = requestBuilder;
        this.scriptExecutor = scriptExecutor;
        this.udpClient = udpClient;
    }


    public void updateData() {
        System.out.println("Загрузка доступных команд с сервера...");

        //запрос на получение доступных команд с сервера
        Request request = new Request("return_map", null, null); //специальный запрос на получение доступных команд

        //получаем доступные команды, которые будут содержаться в startResponse
        Response startResponse = udpClient.sendAndReceive(request);

        if (startResponse == null || !startResponse.isSuccess()) {
            System.err.println("Критическая ошибка! Не получилось получить доступные команды от сервера!");
            System.exit(0);
        }

        System.out.println("Команды успешно загружены!");

        //Инициализируем map из названий всех доступных команд и их подробных описаний
        Map<String, CommandDescriptor> commandDescriptorMap = startResponse.getCommandDescriptorMap();

        //устанавливаем конечный дескриптор команд со всеми командами, доступными пользователю
        requestBuilder.setCommands(commandDescriptorMap);
    }

    /**
     * Метод start вызывается при запуске приложения
     */
    public void start() {
        updateData();

        System.out.println("Введите 'help' для просмотра всех доступных команд.");
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
    public void processInput(Scanner currentScanner, boolean isFileMode) {
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
                    System.out.println("Завершение работы клиента...");
                    break;
                }

                if (request.getCommandName().equals("execute_script")) {
                    scriptExecutor.execute(request);
                }

                else {
                    //отправляем объект запроса на сервер и получаем response
                    Response response = udpClient.sendAndReceive(request);
                    if (response != null) {
                        System.out.println(response.getMessage()); //сообщение от команды

                        Collection<?> collection = response.getCollection();

                        if (collection != null) {
                            for (var element : collection) {
                                System.out.println(element);
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