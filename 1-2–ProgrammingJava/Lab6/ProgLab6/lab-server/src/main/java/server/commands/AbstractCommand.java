package server.commands;

import common.utility.ArgumentType;
import common.utility.CommandDescriptor;
import server.service.CollectionManager;
import server.service.CommandManager;

/**
 * Класс {@code AbstractCommand} - это абстрактная команда с именем, описанием и передаваемым collectionManager
 *
 * @author mikhailkramar
 */
public abstract class AbstractCommand implements Command {
    private final String name;
    private final String description;
    protected final CollectionManager collectionManager;

    public AbstractCommand(String name, String description, CollectionManager collectionManager) {
        this.name = name;
        this.description = description;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean canAcceptArgument(String arg) {
        return true;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArgumentType getArgumentType() {
        return ArgumentType.NONE;
    }

    @Override
    public boolean requiresObjectArgument() {
        return false;
    }

    @Override
    public CommandDescriptor getDescriptor() {
        return new CommandDescriptor(this.name, this.getArgumentType(), this.requiresObjectArgument());
    }
}
