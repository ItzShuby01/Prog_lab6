package org.example.server.commands;

import org.example.common.command.AddCommand;
import org.example.common.data.Coordinates;
import org.example.common.data.Location;
import org.example.common.data.Person;
import org.example.common.response.Response;
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

        String validationError = validatePersonOnServer(personToAdd);
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

    // Helper method for server-side re-validation
    private String validatePersonOnServer(Person person) {
        // Validate name
        if (person.getName() == null || person.getName().isBlank()) {
            return "Person name cannot be empty.";
        }

        // Validate coordinates
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

        // Validate height
        if (Double.isNaN(person.getHeight()) || Double.isInfinite(person.getHeight()) || person.getHeight() <= 0) {
            return "Height must be positive and finite (no NaN/Infinity).";
        }

        // Validate enums (EyeColor, HairColor, Country)
        if (person.getEyeColor() == null) return "Eye color cannot be null.";
        if (person.getHairColor() == null) return "Hair color cannot be null.";

        // Nationality can be null, no validation needed if null.

        // Validate Location if present
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
        return null; // All checks passed
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}