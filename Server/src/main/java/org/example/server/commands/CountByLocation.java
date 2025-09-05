package org.example.server.commands;

import org.example.common.command.CountByLocationCommand;
import org.example.common.data.Location;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;

public class CountByLocation implements ServerCommand {
    public static final String DESCRIPTION = "count_by_location {location}: count the number of elements whose location field value is equal to the given one";
    private final CollectionManager collectionManager;

    public CountByLocation(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Response execute(org.example.common.command.Command commandDto) {
        CountByLocationCommand countCommand = (CountByLocationCommand) commandDto; // Cast to specific DTO
        String locationArg = countCommand.getArg(); // Get the raw location string

        if (locationArg == null || locationArg.trim().isEmpty()) {
            return new Response("Error: Location argument is missing for count_by_location.", false);
        }

        try {
            // THIS IS THE SERVER-SIDE PARSING OF THE LOCATION STRING
            Location locationToCompare = parseLocation(locationArg);

            long count = collectionManager.countByLocation(locationToCompare);
            return new Response("Number of people with location " + locationToCompare + ": " + count, true);
        } catch (IllegalArgumentException e) {
            return new Response("Error parsing location argument: " + e.getMessage(), false);
        } catch (Exception e) {
            System.err.println("Server count_by_location error: " + e.getMessage());
            e.printStackTrace();
            return new Response("An unexpected error occurred while counting by location: " + e.getMessage(), false);
        }
    }

    // Helper method to parse the location string on the server
    private Location parseLocation(String arg) {
        String[] parts = arg.trim().split("\\s+", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Location argument must be in format 'x y name'. Y can be empty if null.");
        }
        try {
            float x = Float.parseFloat(parts[0]);

            Float y = null; // Y can be null
            if (!parts[1].trim().isEmpty()) {
                y = Float.parseFloat(parts[1]);
                if (Float.isNaN(y) || Float.isInfinite(y)) {
                    throw new IllegalArgumentException("Location Y cannot be NaN or Infinity.");
                }
            }

            String name = parts[2];
            if (name.isBlank()) {
                name = null; // Set to null if blank, as it's nullable
            } else if (name.length() > 530) {
                throw new IllegalArgumentException("Location name cannot exceed 530 characters.");
            }

            return new Location(x, y, name);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in location coordinates: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}