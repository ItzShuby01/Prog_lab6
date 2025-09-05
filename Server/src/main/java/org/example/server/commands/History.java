package org.example.server.commands;

import org.example.common.command.HistoryCommand;
import org.example.common.response.Response;
import java.util.List;


public class History implements ServerCommand{
    public static final String DESCRIPTION = "history: print the last 7 commands (without their arguments)";

    // If history is passed directly via execute method
    public History() { }


    public Response execute(HistoryCommand commandDto, List<String> commandHistory) {
        if (commandHistory == null || commandHistory.isEmpty()) {
            return new Response("No commands in history.", true);
        }
        // Return the history list in the data payload of the Response
        return new Response("Last " + commandHistory.size() + " commands:", true, commandHistory);
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}