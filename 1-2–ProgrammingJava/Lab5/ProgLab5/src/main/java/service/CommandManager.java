package service;

import commands.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    //складываем в Map название команды и объект этой команды сразу списком, удобно для сборки в Main
    public void register(Command... cmds) {
        for (Command command : cmds) {
            commands.put(command.getName(), command);
        }
    }

    //взять команду по названию
    public Command getCommand(String name) {
        return commands.get(name);
    }

    //для команды help пригодится
    public Map<String, Command> getCommands() {
        return commands;
    }

}
