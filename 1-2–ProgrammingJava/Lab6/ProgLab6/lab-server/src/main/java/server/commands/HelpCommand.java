package server.commands;

import common.transport.Response;
import server.service.CollectionManager;
import common.transport.Request;

import java.util.Map;

/**
 * Класс {@code HelpCommand} описывает команду "help", которая позволяет получить все команды и их описание
 *
 * @author mikhailkramar
 */
public class HelpCommand extends AbstractCommand {
    //ссылка на все команды, которые есть в CommandManager, в конструкторе как раз всё ей передадим
    private Map<String, Command> commands;

    public HelpCommand(CollectionManager cm) {
        super("help", "вывести справку по доступным командам", cm);
    }

    public void setCommands(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public Response execute(Request request) {
        if (commands.isEmpty()) {
            return new Response(true, null,"Список команд пока пуст!");
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("–––– Справка по всем командам ––––\n");

        //теперь идем по Map, получая ключ и значение через лямбду
        commands.forEach((name, command) -> {
            stringBuilder.append(name).append(": ").append(command.getDescription()).append("\n");
        });

        stringBuilder.append("––––––––––––––––––––––––––––––––––");

        return new Response(true, null, stringBuilder.toString());
    }
}
