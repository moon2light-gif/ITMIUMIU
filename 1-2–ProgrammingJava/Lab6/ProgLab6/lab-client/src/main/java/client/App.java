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
import java.util.SortedMap;

/**
 * Класс {@code Client} объединяет RequestBuilder и UDPClient
 * <p>
 * author mikhailkramar
 */

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ElementReader elementReader = new ElementReader(scanner);
        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //Выставляем UDP-клиента
        Client udpClient = new UDPClient("localhost", 19615);
        Client wrappedClient = new RetryClientDecorator(udpClient);

        //Инициализируем map из названий всех доступных команд и их подробных описаний
        Map<String, CommandDescriptor> commandDescriptorMap = new HashMap<>();

        //Инициализируем сборщика запросов от клиента
        RequestBuilder requestBuilder = new RequestBuilder(elementReader, commandDescriptorMap);
        requestBuilder.setUdpClient(wrappedClient);

        elementReader.setRequestBuilder(requestBuilder);

        //Инициализируем менеджер консоли пользователя
        ConsoleManager consoleManager = new ConsoleManager(
                elementReader,
                scanner,
                requestBuilder,
                scriptExecutor,
                wrappedClient
        );

        scriptExecutor.setConsoleManager(consoleManager);

        //Запуск основной программы
        consoleManager.start();
    }
}
