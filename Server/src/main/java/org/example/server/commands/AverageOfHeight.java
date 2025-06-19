package org.example.server.commands;

import org.example.common.command.AverageOfHeightCommand;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;

public class AverageOfHeight implements ServerCommand{
    public static final String DESCRIPTION = "average_of_height: output the average height field value for all elements in a collection";
    private final CollectionManager collectionManager;

    public AverageOfHeight(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    // Executes the 'average_of_height' command on the server.
    // Takes an AverageOfHeightCommand DTO and returns a Response DTO.

    public Response execute(AverageOfHeightCommand commandDto) {
        try {
            double average = collectionManager.getAverageHeight();
            return new Response(String.format("Average height: %.2f", average), true);
        } catch (IllegalStateException e) {
            return new Response(e.getMessage(), false);
        }
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}