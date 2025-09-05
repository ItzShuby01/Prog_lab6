package org.example.server.commands;

import org.example.common.command.AddIfMaxCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.common.util.ValidationUtil;
import org.example.server.manager.CollectionManager;

public class AddIfMax implements ServerCommand {
    private static final String DESCRIPTION = "add_if_max {element}: add a new element to a collection if its value is greater than the value of the largest element in that collection";


    private final CollectionManager collectionManager;

    public AddIfMax(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

     // Executes the 'add_if_max' command on the server.
     // Takes an AddIfMaxCommand DTO and returns a Response DTO.

    public Response execute(AddIfMaxCommand commandDto) {
        Person newPerson = commandDto.getPerson();

        if (newPerson == null) {
            return new Response("Error: Person object is missing from the command.", false);
        }
        // Server-side validation of the incoming Person data
        String validationError = ValidationUtil.validatePerson(newPerson);
        if (validationError != null) {
            return new Response("Validation Error: " + validationError, false);
        }
        double currentMaxHeight = collectionManager.getMaxHeight();

        if (newPerson.getHeight() > currentMaxHeight) {
            newPerson.setId(collectionManager.generateId());
            if (collectionManager.addPerson(newPerson)) {
                return new Response(
                        "ADDED: " + newPerson.getName() + " (ID: " + newPerson.getId() + ") as its height ("
                                + newPerson.getHeight() + ") > max height (" + currentMaxHeight + ").",
                        true
                );
            } else {
                return new Response("Failed to add person: " + newPerson.getName() + ". Possible internal error.", false);
            }
        } else {
            return new Response(
                    newPerson.getName() + " NOT ADDED as its height (" + newPerson.getHeight()
                            + ") ≤ max height (" + currentMaxHeight + ").",
                    true
            );
        }
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}