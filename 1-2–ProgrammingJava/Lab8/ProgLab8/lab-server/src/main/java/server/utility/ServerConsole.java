package server.utility;

import common.transport.Request;
import common.transport.Response;
import server.commands.Command;
import server.service.CommandManager;
import server.service.RequestExecutor;

import java.io.IOException;
import java.util.Scanner;

/**
 * Класс {@code ServerConsole} отвечает за работу консоли сервера
 *
 * @author mikhailkramar
 */
public class ServerConsole {
    private final Scanner scanner;
    private final CommandManager commandManager;

    public ServerConsole(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Метод, отвечающий за считывание команды из консоли сервера
     * @throws IOException
     */
    public void checkCommandLine() throws IOException {
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim().toLowerCase();
            handleServerCommand(line);
        }
        else {
            System.exit(0);
        }
    }

    /**
     * Метод для соотношения введенной команды и доступных команд серверу
     * @param commandName имя полученной команды
     */
    private void handleServerCommand(String commandName) {

            Command command = commandManager.getCommand(commandName);

             if (command == null) {
                 System.out.println("Ошибка: такая команда не найдена на сервере!");
                 return;
             }

            Response response = command.execute(new Request(null, null, command.getName(),null, null));
            System.out.print(response.getMessage());
    }

}
