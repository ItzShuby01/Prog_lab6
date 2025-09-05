package org.example.client.util;

import org.example.common.data.*;
import org.example.common.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;


//  A utility class to build a Person object from script file inputs.

public class PersonBuilder {

    // Constructs a Person object from a list of script lines.
    public static Person buildFromScript(List<String> data) throws IllegalArgumentException {
        if (data.size() < 7) { // A minimum of 7 required fields
            throw new IllegalArgumentException("Insufficient data to build a Person from script. Expected at least 7 fields.");
        }

        try {
            // Read and validate required fields
            String name = ValidationUtil.validateString(data.get(0), "name");
            int x = ValidationUtil.validateInt(data.get(1), "x (int)");
            double y = ValidationUtil.validateDouble(data.get(2), "y (double)");
            double height = ValidationUtil.validateDouble(data.get(3), "height (double)");
            EyeColor eyeColor = EyeColor.valueOf(data.get(4).toUpperCase());
            HairColor hairColor = HairColor.valueOf(data.get(5).toUpperCase());

            // Handle optional fields: Nationality and Location
            Country nationality = null;
            if (!data.get(6).isBlank()) {
                nationality = Country.valueOf(data.get(6).toUpperCase());
            }

            Location location = null;
            // A location requires at least 3 inputs
            if (data.size() >= 10 && !(data.get(7).isBlank() && data.get(8).isBlank() && data.get(9).isBlank())) {
                float locationX = ValidationUtil.validateFloat(data.get(7), "location x (float)");
                float locationY = ValidationUtil.validateFloat(data.get(8), "location y (float)");
                String locationName = ValidationUtil.validateString(data.get(9), "location name");
                location = new Location(locationX, locationY, locationName);
            }

            Coordinates coordinates = new Coordinates(x, y);

            Person person = new Person(null, name, coordinates, LocalDateTime.now(), height, eyeColor, hairColor, nationality, location);

            // Final validation with a dedicated  method from ValidationUtil utility class
            String validationError = ValidationUtil.validatePerson(person);
            if (validationError != null) {
                throw new IllegalArgumentException("Validation failed for person from script: " + validationError);
            }

            return person;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error parsing person data from script: " + e.getMessage());
        }
    }
}
