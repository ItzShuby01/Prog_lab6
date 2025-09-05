package org.example.server.commands;

import org.example.common.command.HelpCommand;
import org.example.common.response.Response;
import org.example.server.manager.ServerCommandManager;

import java.util.Map;
import java.util.stream.Collectors;

public class Help implements ServerCommand {
    public static final String DESCRIPTION = "help: display help on available commands.";

    private final ServerCommandManager serverCommandManager;

    public Help(ServerCommandManager serverCommandManager) {
        this.serverCommandManager = serverCommandManager;
    }

    // The execute method only needs the commandDto
    public Response execute(HelpCommand commandDto) {
        // Get descriptions from the ServerCommandExecutor
        Map<String, String> descriptions = serverCommandManager.getCommandDescriptions();

        if (descriptions == null || descriptions.isEmpty()) {
            return new Response("No command descriptions available on the server.", true);
        }
        // Build the help message dynamically from the map
        String helpText = descriptions.entrySet().stream()
                .map(entry -> String.format("%-25s: %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));

        return new Response("Available commands:\n" + helpText, true);
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}