package org.example.server.commands;

import org.example.common.command.SaveCommand;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;
import org.example.server.manager.FileManager;

public class Save implements ServerCommand{
    public static final String DESCRIPTION = "save: save collection to file";

    private final CollectionManager collectionManager;
    private final FileManager fileManager;

    public Save(CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    public Response execute(SaveCommand commandDto) {

        try {
            String filePath = System.getenv("COLLECTION_FILE_PATH");
            fileManager.saveCollectionToXml(filePath);
            return new Response("Collection saved to file successfully.", true);
        } catch (Exception e) {
            return new Response("Error saving collection to file: " + e.getMessage(), false);
        }
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}