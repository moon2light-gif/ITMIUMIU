import commands.ExecuteScriptCommand;
import exceptions.EnvNotFoundException;
import service.CollectionManager;
import service.CommandManager;
import service.DumpManager;
import service.RequestExecutor;
import utility.ConsoleManager;
import utility.ElementReader;
import commands.*;

import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        String filePath = System.getenv("PATH_TO_XML"); //поиск переменной под именем PATH_TO_XML

        if (filePath == null || filePath.isEmpty()) {
            throw new EnvNotFoundException();
        }

        //инициализируем наши инструменты
        DumpManager dumpManager = new DumpManager(filePath);
        CollectionManager collectionManager = new CollectionManager(dumpManager);
        CommandManager commandManager = new CommandManager();

        //загрузка в коллекцию
        dumpManager.load(collectionManager);

        //регистрация команд
        ExecuteScriptCommand executeScriptCommand = new ExecuteScriptCommand(collectionManager);
        commandManager.register(
                new AddCommand(collectionManager),
                new AddIfMinCommand(collectionManager),
                new ClearCommand(collectionManager),
                executeScriptCommand,
                new ExitCommand(collectionManager),
                new FilterLessThanAnnualTurnoverCommand(collectionManager),
                new HeadCommand(collectionManager),
                new HelpCommand(collectionManager),
                new InfoCommand(collectionManager),
                new PrintFieldAscendingAnnualTurnoverCommand(collectionManager),
                new PrintUniqueOfficialAddressCommand(collectionManager),
                new RemoveByIdCommand(collectionManager),
                new RemoveFirstCommand(collectionManager),
                new SaveCommand(collectionManager),
                new ShowCommand(collectionManager),
                new UpdateCommand(collectionManager)
        );

        HelpCommand help = (HelpCommand) commandManager.getCommand("help");
        help.setCommands(commandManager.getCommands());

        Scanner scanner = new Scanner(System.in); //по умолчанию консольный ввод
        ElementReader elementReader = new ElementReader(scanner);
        RequestExecutor requestExecutor = new RequestExecutor(commandManager);
        ConsoleManager consoleManager = new ConsoleManager(
                commandManager,
                collectionManager,
                elementReader,
                scanner,
                requestExecutor
        );

        executeScriptCommand.setConsoleManager(consoleManager);

        System.out.println("Программа запущена! Введите 'help' для просмотра доступных команд.");
        consoleManager.start();
    }
}