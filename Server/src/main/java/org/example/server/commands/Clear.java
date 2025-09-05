package org.example.server.commands;

import org.example.common.command.ClearCommand;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;

public class Clear implements ServerCommand {
    public static final String DESCRIPTION = "clear: clear collection";

    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    // Executes the 'clear' command on the server.
    // Takes a ClearCommand DTO and returns a Response DTO.

    public Response execute(ClearCommand commandDto) {
        collectionManager.clear();
        return new Response("Collection has been cleared successfully.", true);
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}