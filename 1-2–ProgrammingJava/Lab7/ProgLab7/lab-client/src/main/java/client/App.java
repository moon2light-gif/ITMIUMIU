package client;

import client.commands.ScriptExecutor;
import client.connection.Client;
import client.connection.RetryClientDecorator;
import client.connection.UDPClient;
import client.utility.*;
import common.transport.DescriptorMapResponse;
import common.transport.Request;
import common.transport.Response;
import common.utility.ArgumentType;
import common.utility.CommandDescriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Класс {@code App} объединяет все нужные нам классы в одно приложение
 * <p>
 * author mikhailkramar
 */
public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ElementReader elementReader = new ElementReader(scanner);
        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //Выставляем UDP-клиента
        Client udpClient = new UDPClient("localhost", 19614);
        Client wrappedClient = new RetryClientDecorator(udpClient);

        //Инициализируем менеджера авторизации пользователя
        AuthManager authManager = new AuthManager(scanner, wrappedClient);
        authManager.runAuthorizationMenu(); //запускаем меню авторизации пользователя

        Map<String, CommandDescriptor> commandDescriptorMap = new HashMap<>();

        //Инициализируем сборщика запросов от клиента
        RequestBuilder requestBuilder = new RequestBuilder(elementReader, commandDescriptorMap);
        requestBuilder.setUdpClient(wrappedClient);

        elementReader.setRequestBuilder(requestBuilder);

        //Инициализируем консоль клиента
        ConsoleManager consoleManager = new ConsoleManager(
                elementReader,
                scanner,
                requestBuilder,
                scriptExecutor,
                wrappedClient,
                authManager
        );

        scriptExecutor.setConsoleManager(consoleManager);

        //Запуск основной программы
        consoleManager.start();
    }
}
