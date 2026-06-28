package server.commands;

import common.transport.DescriptorMapResponse;
import common.transport.Request;
import common.transport.Response;
import common.utility.CommandDescriptor;

import java.util.HashMap;
import java.util.Map;

public class ReturnDescriptorMapCommand extends AbstractCommand {
    private final Map<String, Command> commands;

    public ReturnDescriptorMapCommand(Map<String, Command> commands) {
        super("return_map", "возвращает доступные клиенту команды", null);
        this.commands = new HashMap<>(commands);
    }


    @Override
    public Response execute(Request request) {
        Map<String, CommandDescriptor> commandDescriptorMap = new HashMap<>();
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            commandDescriptorMap.put(entry.getKey(), entry.getValue().getDescriptor());
        }

        return new DescriptorMapResponse(true, "Доступные команды.", commandDescriptorMap);
    }
}
