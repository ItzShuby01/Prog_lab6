package org.example.server.commands;

import org.example.common.command.ShowCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;

import java.util.List;

public class Show implements ServerCommand{
    private static final String DESCRIPTION = "show: print all elements of the collection to standard output";
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

     //Takes a ShowCommand DTO and returns a Response DTO with the list of persons.

    public Response execute(ShowCommand commandDto) {
        List<Person> persons = collectionManager.getAllPersons();
        if (persons.isEmpty()) {
            return new Response("The collection is empty.", true);
        }

        // Return the list of persons in the data payload
        return new Response("DISPLAYING THE COLLECTION DATA:", true, persons);
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}