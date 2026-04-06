package service;

import commands.Command;
import transport.Request;

public class RequestExecutor {
    private final CommandManager commandManager;

    public RequestExecutor(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void execute(Request request) {
        if (request == null) return;
        Command command = commandManager.getCommand(request.getCommandName());
        command.execute(request);
    }
}
