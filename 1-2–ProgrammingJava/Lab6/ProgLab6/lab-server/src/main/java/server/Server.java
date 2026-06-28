package server;

import server.exceptions.EnvNotFoundException;
import server.service.CollectionManager;
import server.service.CommandManager;
import server.service.DumpManager;

import server.commands.*;
import server.service.RequestExecutor;
import server.utility.ServerConsole;
import server.utility.UDPServer;

/**
 * Класс {@code Server} отвечает за инициализацию сетевого сервера,
 * загружает коллекцию
 *
 * @author mikhailkramar
 */
public final class Server {

    public static void main(String[] args) {
        String filePath = System.getenv("PATH_TO_XML");

        if (filePath == null || filePath.isEmpty()) {
            throw new EnvNotFoundException();
        }

        DumpManager dumpManager = new DumpManager(filePath);
        CollectionManager collectionManager = new CollectionManager(dumpManager);
        CommandManager commandManager = new CommandManager();
        CommandManager serverCommandManager = new CommandManager();
        ServerConsole serverConsole = new ServerConsole(serverCommandManager);

        //загрузка содержимого исходного файла в коллекцию
        dumpManager.load(collectionManager);

        //заносим доступные клиенту команды
        commandManager.register(
                new AddCommand(collectionManager),
                new AddIfMinCommand(collectionManager),
                new ClearCommand(collectionManager),
                new FilterLessThanAnnualTurnoverCommand(collectionManager),
                new HeadCommand(collectionManager),
                new HelpCommand(collectionManager),
                new InfoCommand(collectionManager),
                new PrintFieldAscendingAnnualTurnoverCommand(collectionManager),
                new PrintUniqueOfficialAddressCommand(collectionManager),
                new RemoveByIdCommand(collectionManager),
                new RemoveFirstCommand(collectionManager),
                new ShowCommand(collectionManager),
                new UpdateCommand(collectionManager),
                new ExitCommand(collectionManager)
        );

        HelpCommand help = (HelpCommand) commandManager.getCommand("help");
        help.setCommands(commandManager.getCommands());

        //добавляем команду, которая отправляется клиентом при включении
        ReturnDescriptorMapCommand returnDescriptorMapCommand = new ReturnDescriptorMapCommand(commandManager.getCommands());
        commandManager.register(returnDescriptorMapCommand);

        //регистрируем команды, доступные серверу
        serverCommandManager.register(
                new ExitCommand(collectionManager),
                new SaveCommand(collectionManager)
        );

        RequestExecutor requestExecutor = new RequestExecutor(commandManager, collectionManager);

        int PORT = 19615;
        UDPServer udpServer = new UDPServer(PORT, requestExecutor, serverConsole);

        //запуск серверного UDP-канала
        udpServer.start();

    }

}
