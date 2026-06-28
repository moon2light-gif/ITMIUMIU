package server;

import server.database.ConnectionPool;
import server.database.OrganizationDatabaseManager;
import server.database.DatabaseConnector;
import server.database.UserManager;
import server.service.CollectionManager;
import server.service.CommandManager;

import server.commands.*;
import server.service.RequestExecutor;
import server.utility.ServerConsole;
import server.utility.UDPServer;

import java.io.IOException;
import java.io.ObjectOutput;
import java.net.*;
import java.util.Scanner;

/**
 * Класс {@code Server} отвечает за инициализацию сетевого сервера,
 * загружает коллекцию
 *
 * @author mikhailkramar
 */
public final class Server {

    public static void main(String[] args) {

        CollectionManager collectionManager = new CollectionManager();
        CommandManager commandManager = new CommandManager();
        CommandManager serverCommandManager = new CommandManager();
        ServerConsole serverConsole = new ServerConsole(serverCommandManager);

        //Инициализация объектов для работы с БД
        DatabaseConnector databaseConnector = new DatabaseConnector();
        ConnectionPool connectionPool = new ConnectionPool(databaseConnector);
        UserManager userManager = new UserManager(connectionPool);
        OrganizationDatabaseManager organizationDatabaseManager = new OrganizationDatabaseManager(connectionPool, userManager);

        //Загрузка содержимого БД в коллекцию
        organizationDatabaseManager.load(collectionManager);

        //Регистрируем доступные пользователю команды
        commandManager.register(
                new AddCommand(collectionManager, organizationDatabaseManager),
                new AddIfMinCommand(collectionManager, organizationDatabaseManager),
                new ClearCommand(collectionManager, organizationDatabaseManager, userManager),
                new FilterLessThanAnnualTurnoverCommand(collectionManager),
                new HeadCommand(collectionManager),
                new HelpCommand(collectionManager),
                new InfoCommand(collectionManager),
                new PrintFieldAscendingAnnualTurnoverCommand(collectionManager),
                new PrintUniqueOfficialAddressCommand(collectionManager),
                new RemoveByIdCommand(collectionManager, organizationDatabaseManager, userManager),
                new RemoveFirstCommand(collectionManager, organizationDatabaseManager),
                new ShowCommand(collectionManager),
                new UpdateCommand(collectionManager, organizationDatabaseManager, userManager)
        );

        HelpCommand help = (HelpCommand) commandManager.getCommand("help");
        help.setCommands(commandManager.getCommands());

        //Регистрируем команду, которая отправляется клиентом при запуске
        ReturnDescriptorMapCommand returnDescriptorMapCommand = new ReturnDescriptorMapCommand(commandManager.getCommands());
        commandManager.register(returnDescriptorMapCommand);

        //Добавляем в список команды для работы с конкретным пользователем (для БД)
        commandManager.register(
                new LoginCommand(userManager),
                new RegisterCommand(userManager)
        );

        //Регистрируем команды, доступные серверу
        serverCommandManager.register(
                new ExitCommand(collectionManager)
        );

        RequestExecutor requestExecutor = new RequestExecutor(commandManager, collectionManager);

        int PORT = 19614;
        UDPServer udpServer = new UDPServer(PORT, requestExecutor, serverConsole);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nНачинаем безопасное закрытие ресурсов сервера...");
            //тушу все пулы потоков и пул соединений, а также закрываю DatagramChannel
            udpServer.stop();
            connectionPool.shutdown();

            System.out.println("Сервер успешно и безопасно завершил свою работу.");
        }, "ShutdownHookThread"));

        //запуск серверного UDP-канала
        try {
            udpServer.start();
        } catch (IOException ex) {
            System.err.println("Критическая ошибка при старте сервера: " + ex.getMessage());
        }

    }
}
