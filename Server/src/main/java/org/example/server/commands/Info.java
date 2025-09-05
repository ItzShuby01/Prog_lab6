package org.example.server.commands;

import org.example.common.command.InfoCommand;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;

public class Info implements ServerCommand{
    public static final String DESCRIPTION = "info: print collection information (type, initialization date, number of elements, etc.) to standard output";

    private final CollectionManager collectionManager;

    public Info(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Response execute(InfoCommand commandDto) {
        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("Collection Type: ").append(collectionManager.getCollectionType()).append("\n");
        infoBuilder.append("Initialization Date: ").append(collectionManager.getInitializationDate()).append("\n");
        infoBuilder.append("Number of persons in collection: ").append(collectionManager.getElementCount());

        return new Response(infoBuilder.toString(), true);
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}