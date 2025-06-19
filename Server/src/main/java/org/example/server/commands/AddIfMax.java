package org.example.server.commands;

import org.example.common.command.AddIfMaxCommand;
import org.example.common.data.Coordinates;
import org.example.common.data.Location;
import org.example.common.data.Person;
import org.example.common.response.Response;
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
        String validationError = validatePersonOnServer(newPerson);
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
                            + ") \u2264 max height (" + currentMaxHeight + ").", // Using unicode for less-than-or-equal
                    true
            );
        }
    }

    private String validatePersonOnServer(Person person) {
        if (person.getName() == null || person.getName().isBlank()) {
            return "Person name cannot be empty.";
        }
        Coordinates coords = person.getCoordinates();
        if (coords == null) {
            return "Person coordinates cannot be null.";
        }
        if (coords.getX() > 629) {
            return "Coordinate x must be less than 630 (max 629).";
        }
        if (Double.isNaN(coords.getY()) || Double.isInfinite(coords.getY())) {
            return "Coordinate y cannot be NaN or Infinity.";
        }
        if (Double.isNaN(person.getHeight()) || Double.isInfinite(person.getHeight()) || person.getHeight() <= 0) {
            return "Height must be positive and finite (no NaN/Infinity).";
        }
        if (person.getEyeColor() == null) return "Eye color cannot be null.";
        if (person.getHairColor() == null) return "Hair color cannot be null.";
        Location loc = person.getLocation();
        if (loc != null) {
            if (Float.isNaN(loc.getX()) || Float.isInfinite(loc.getX())) {
                return "Location x cannot be NaN or Infinity.";
            }
            if (Float.isNaN(loc.getY()) || Float.isInfinite(loc.getY())) {
                return "Location y cannot be NaN or Infinity.";
            }
            if (loc.getName() == null || loc.getName().isBlank()) {
                return "Location name cannot be empty if location is provided.";
            }
            if (loc.getName().length() > 530) {
                return "Location name cannot exceed 530 characters.";
            }
        }
        return null;
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}