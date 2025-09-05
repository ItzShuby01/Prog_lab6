package org.example.server.commands;

import org.example.common.command.AddCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.common.util.ValidationUtil;
import org.example.server.manager.CollectionManager;

public class Add implements ServerCommand {
    public static final String DESCRIPTION = "add {element}: add a new item to the collection";

    private final CollectionManager collectionManager;

    public Add(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

     // Executes the 'add' command on the server.
     // Takes an AddCommand DTO and returns a Response DTO.

    public Response execute(AddCommand commandDto) {
        Person personToAdd = commandDto.getPerson();

        if (personToAdd == null) {
            return new Response("Error: Person object is missing from the command.", false);
        }

        String validationError = ValidationUtil.validatePerson(personToAdd);
        if (validationError != null) {
            return new Response("Validation Error: " + validationError, false);
        }
        // Generate a new ID on the server
        personToAdd.setId(collectionManager.generateId());
        if (collectionManager.addPerson(personToAdd)) {
            return new Response(personToAdd.getName() + " (ID: " + personToAdd.getId() + ") added to collection.", true);
        } else {
            return new Response("Failed to add person: " + personToAdd.getName() + ". Possible duplicate, or internal error.", false);
        }
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}