package org.example.server.commands;

import org.example.common.command.MaxByIdCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;

public class MaxById implements ServerCommand {
    public static final String DESCRIPTION = "max_by_id: output any object from the collection whose id field value is maximum";

    private final CollectionManager collectionManager;

    public MaxById(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Response execute(MaxByIdCommand commandDto) {
        Person maxPerson = collectionManager.getMaxById();
        if (maxPerson != null) {
            // Return the Person object in the data payload
            return new Response("Person with maximum ID:", true, maxPerson);
        } else {
            return new Response("Collection is empty. No person with maximum ID found.", true); // Still true, it's just no data
        }
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}