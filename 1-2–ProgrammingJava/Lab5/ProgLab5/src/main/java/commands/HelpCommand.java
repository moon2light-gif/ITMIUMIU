package commands;

import service.CollectionManager;
import transport.Request;
import java.util.Map;

public class HelpCommand extends AbstractCommand {
    //ссылка на все команды, которые есть в CommandManager, в конструкторе как раз всё ей передадим
    private Map<String, Command> commands;

    public HelpCommand(CollectionManager cm) {
        super("help", "вывести справку по доступным командам", cm);
    }

    public void setCommands(Map<String, Command> commands) {
        this.commands = commands;
    }

    //NONE и false

    @Override
    public void execute(Request request) {
        if (commands.isEmpty()) {
            System.out.println("Список команд пока пуст!");
            return; //сразу завершаем выполнение
        }

        System.out.println("–––– Справка по всем командам ––––");
        //теперь идем по Map, получая ключ и значение через лямбду
        commands.forEach((name, command) -> {
            System.out.println(name + ": " + command.getDescription());
        });
        System.out.println("––––––––––––––––––––––––––––––––––");
    }
}
