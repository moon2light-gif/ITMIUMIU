package commands;

import service.CollectionManager;
import utility.ArgumentType;

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
}
