package org.example.server.commands;

import org.example.common.command.UpdateCommand;
import org.example.common.data.Coordinates;
import org.example.common.data.Location;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;

public class Update implements ServerCommand{
    public static final String DESCRIPTION = "update id {element}: update the value of the collection element whose id is equal to the given one";

    private final CollectionManager collectionManager;

    public Update(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Response execute(UpdateCommand commandDto) {
        String idArg = commandDto.getArg();
        Person updatedPerson = commandDto.getPerson(); // This is the new Person data

        if (idArg == null || idArg.trim().isEmpty()) {
            return new Response("Error: ID argument is missing for update.", false);
        }
        if (updatedPerson == null) {
            return new Response("Error: Person object for update is missing from the command.", false);
        }

        String trimmedIdArg = idArg.trim();

        // Server-side validation of ID format
        if (!trimmedIdArg.matches("[1-9]\\d*")) {
            return new Response("Invalid ID format: ID must be a positive integer.", false);
        }

        try {
            int id = Integer.parseInt(trimmedIdArg);
            updatedPerson.setId(id); // Setting the ID of the new person data to match the target ID

            // Server-side validation of the incoming updated Person data
            String validationError = validatePersonOnServer(updatedPerson);
            if (validationError != null) {
                return new Response("Validation Error for updated person data: " + validationError, false);
            }

            // Perform the update
            if (collectionManager.updatePerson(id, updatedPerson)) {
                return new Response("Person with ID " + id + " updated successfully.", true);
            } else {
                Person existing = collectionManager.getById(id);
                if (existing == null) {
                    return new Response("Person with ID " + id + " not found in the collection. Cannot update.", true);
                } else {
                    return new Response("Failed to update person with ID " + id + ". Possible internal error or no changes detected.", false);
                }
            }
        } catch (NumberFormatException e) {
            return new Response("Invalid ID format: " + e.getMessage(), false);
        } catch (Exception e) {
            return new Response("An unexpected error occurred while updating: " + e.getMessage(), false);
        }
    }

    // Helper method for server-side validation
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