package org.example.server.commands;

import org.example.common.command.RemoveLowerCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.common.util.ValidationUtil;
import org.example.server.manager.CollectionManager;

import java.util.List;

public class RemoveLower implements ServerCommand {
    public static final String DESCRIPTION = "remove_lower {element}: remove all elements from the collection that are less than the specified number";

    private final CollectionManager collectionManager;

    public RemoveLower(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Response execute(RemoveLowerCommand commandDto) {
        Person thresholdPerson = commandDto.getPerson();

        if (thresholdPerson == null) {
            return new Response("Error: Person object for comparison is missing from the command.", false);
        }

        // Server-side Re-validation of the incoming Person data
        String validationError = ValidationUtil.validatePerson(thresholdPerson);
        if (validationError != null) {
            return new Response("Validation Error for threshold person: " + validationError, false);
        }

        try {
            List<Person> removedPersons = collectionManager.removeLower(thresholdPerson);
            return new Response("Removed " + removedPersons.size() + " persons.", true);
        } catch (Exception e) {
            return new Response("Error during remove_lower command: " + e.getMessage(), false);
        }
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}