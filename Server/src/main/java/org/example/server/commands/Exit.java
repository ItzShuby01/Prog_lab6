package org.example.server.commands;

import org.example.common.command.ExitCommand;
import org.example.common.response.Response;

public class Exit implements ServerCommand{
    public static final String DESCRIPTION = "exit: exit the program (without saving to file";

    public Exit() {}

    public Response execute(ExitCommand commandDto) {
        return new Response("Server received exit command. Client should terminate.", true);
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}