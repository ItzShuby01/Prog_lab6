package org.example.common.util;

import org.example.common.data.Country;
import org.example.common.data.EyeColor;
import org.example.common.data.HairColor;

import java.util.Arrays;


public class ValidationUtil {

    public static final String[] INPUTS_LABELS = {
            "name (string)",
            "x (int)",
            "y (double)",
            "height (double)",
            "eyeColor (valid : RED/BLACK/ORANGE)",
            "hairColor (valid: GREEN/BLUE/YELLOW/BROWN)",
            "nationality (valid: RUSSIA/GERMANY/ITALY/THAILAND/JAPAN)",
            "locationX (float)",
            "locationY (float)",
            "locationName (string)"
    };


    public static <T extends Enum<T>> String validateEnum(Class<T> enumClass, String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            return fieldName + " cannot be empty.";
        }
        try {
            Enum.valueOf(enumClass, value.toUpperCase());
            return null; // Valid
        } catch (IllegalArgumentException e) {
            return "Invalid " + fieldName + ": '" + value + "'. Valid options: " + Arrays.toString(enumClass.getEnumConstants());
        }
    }

    public static String validateInput(int index, String value) {
        if (value == null) {
            return INPUTS_LABELS[index] + " is missing.";
        }
        try {
            return switch (index) {
                case 0 -> value.isBlank() ? "Name cannot be empty." : null;
                case 1 -> {
                    int parsedX = Integer.parseInt(value);
                    if (parsedX > 629) {
                        yield "X cannot be greater than 629.";
                    }
                    yield null;
                }
                case 2 -> { // y (double)
                    double parsedY = Double.parseDouble(value);
                    if (Double.isNaN(parsedY) || Double.isInfinite(parsedY)) {
                        yield "Y coordinate cannot be NaN or Infinity.";
                    }
                    yield null;
                }
                case 3 -> { // height (double)
                    double parsedHeight = Double.parseDouble(value);
                    if (parsedHeight <= 0 || Double.isNaN(parsedHeight) || Double.isInfinite(parsedHeight)) {
                        yield "Height must be positive and finite.";
                    }
                    yield null;
                }
                case 4 -> validateEnum(EyeColor.class, value, "Eye color"); // eyeColor
                case 5 -> validateEnum(HairColor.class, value, "Hair color"); // hairColor
                case 6 -> { // nationality (can be empty)
                    if (value.trim().isEmpty()) {
                        yield null;
                    }
                    yield validateEnum(Country.class, value, "Nationality");
                }
                case 7 -> { // locationX (float)
                    if (value.trim().isEmpty()) {
                        yield null; // Valid if empty for location
                    }
                    float parsedLocationX = Float.parseFloat(value);
                    if (Float.isNaN(parsedLocationX) || Float.isInfinite(parsedLocationX)) {
                        yield "Location X cannot be NaN or Infinity.";
                    }
                    yield null;
                }
                case 8 -> { // locationY (float)
                    if (value.trim().isEmpty()) {
                        yield null; // Valid if empty for location
                    }
                    float parsedLocationY = Float.parseFloat(value);
                    if (Float.isNaN(parsedLocationY) || Float.isInfinite(parsedLocationY)) {
                        yield "Location Y cannot be NaN or Infinity.";
                    }
                    yield null;
                }
                case 9 -> { // locationName (string)
                    if (value.trim().isEmpty()) {
                        yield null;
                    }
                    if (value.isBlank()) {
                        yield "Location name cannot be empty.";
                    }
                    yield null;
                }
                default -> "Invalid field index.";
            };
        } catch (NumberFormatException e) {
            return "Invalid number format for " + INPUTS_LABELS[index] + ".";
        }
    }

    //  Methods for direct validation  for script parsing on server
    public static int validateInt(String value, String fieldName) throws IllegalArgumentException {
        try {
            int parsed = Integer.parseInt(value);
            if (fieldName.equals(INPUTS_LABELS[1]) && parsed > 629) {
                throw new IllegalArgumentException(fieldName + " cannot be greater than 629.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format for " + fieldName + ".");
        }
    }

    public static double validateDouble(String value, String fieldName) throws IllegalArgumentException {
        try {
            double parsed = Double.parseDouble(value);
            if (Double.isNaN(parsed) || Double.isInfinite(parsed)) {
                throw new IllegalArgumentException(fieldName + " cannot be NaN or Infinity.");
            }
            if (fieldName.equals(INPUTS_LABELS[3]) && parsed <= 0) {
                throw new IllegalArgumentException("Height must be positive.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format for " + fieldName + ".");
        }
    }

    public static float validateFloat(String value, String fieldName) throws IllegalArgumentException {
        try {
            float parsed = Float.parseFloat(value);
            if (Float.isNaN(parsed) || Float.isInfinite(parsed)) {
                throw new IllegalArgumentException(fieldName + " cannot be NaN or Infinity.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format for " + fieldName + ".");
        }
    }

    public static String validateString(String value, String fieldName) throws IllegalArgumentException {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        if (fieldName.equals(INPUTS_LABELS[9]) && value.length() > 530) {
            throw new IllegalArgumentException("Location name cannot exceed 530 characters.");
        }
        return value;
    }
}