package org.example.server.commands;

import org.example.common.command.RemoveByIdCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;

public class RemoveById implements ServerCommand{
    public static final String DESCRIPTION = "remove_by_id id: remove an element from a collection by its id";

    private final CollectionManager collectionManager;

    public RemoveById(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Response execute(RemoveByIdCommand commandDto) {
        String arg = commandDto.getArg();

        if (arg == null || arg.trim().isEmpty()) {
            return new Response("Error: ID argument is missing for remove_by_id.", false);
        }

        String trimmedArg = arg.trim();

        // Server-side validation of ID format to allow only positive integers
        if (!trimmedArg.matches("[1-9]\\d*")) {
            return new Response("Invalid ID format: ID must be a positive integer.", false);
        }

        try {
            int id = Integer.parseInt(trimmedArg);
            Person personToRemove = collectionManager.getById(id);

            if (personToRemove != null) {
                if (collectionManager.removePerson(personToRemove)) {
                    return new Response("Person with ID " + id + " removed successfully.", true);
                } else {
                    return new Response("Failed to remove person with ID " + id + ". Possible internal error.", false);
                }
            } else {
                return new Response("ID " + id + " not found in the collection.", true); // Success: command processed, but ID not found.
            }
        } catch (NumberFormatException e) {
            return new Response("Invalid ID format: " + e.getMessage(), false);
        } catch (Exception e) {
            return new Response("An unexpected error occurred while removing by ID: " + e.getMessage(), false);
        }
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}