package commands;

import transport.Request;
import utility.ArgumentType;

public interface Command {
    void execute(Request request);
    String getName();
    String getDescription();

    default boolean canAcceptObject(String arg) {
        return true;
    }

    ArgumentType getArgumentType(); //аргументы команды
    boolean requiresObjectArgument(); //нужен ли команде сложный объект в качестве аргумента
}
