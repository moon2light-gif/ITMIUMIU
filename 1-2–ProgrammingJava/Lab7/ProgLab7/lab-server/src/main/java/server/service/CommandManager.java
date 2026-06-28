package server.service;

import server.commands.Command;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс {@code CommandManager}
 *
 * @author mikhailkramar
 */
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Метод для регистрации команды по имени в Map, которая хранит в качестве значения ключа - объект команды
     *
     * @param cmds массив команд, которые необходимо зарегистрировать
     */
    public void register(Command... cmds) {
        for (Command command : cmds) {
            commands.put(command.getName(), command);
        }
    }

    /**
     * Метод для получения объекта по имени команды
     *
     * @param name имя команды
     * @return объект класса Command
     */
    public Command getCommand(String name) {
        return commands.get(name);
    }

    public Map<String, Command> getCommands() {
        return commands;
    }
}
