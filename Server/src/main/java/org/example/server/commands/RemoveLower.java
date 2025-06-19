package org.example.server.commands;

import org.example.common.command.RemoveLowerCommand;
import org.example.common.data.Coordinates;
import org.example.common.data.Location;
import org.example.common.data.Person;
import org.example.common.response.Response;
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
        String validationError = validatePersonOnServer(thresholdPerson);
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

    //validation logic
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