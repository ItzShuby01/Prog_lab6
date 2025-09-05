package org.example.common.response;

import java.io.Serializable;

   // Represents a response from the server to the client.
  // Contains status, a message, and optionally a data payload.

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String message;
    private final boolean success; // True if command executed successfully, false otherwise
    private final Object data; // To hold a List for commands like 'History'.

    // Constructor for responses with a message and a data payload.
    public Response(String message, boolean success, Object data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }

    //Constructor for responses with only a message (no data payload).
    public Response(String message, boolean success) {
        this(message, success, null); // Calls the other constructor with null data
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(success ? "[SUCCESS] " : "[ERROR] ").append(message);
        if (data != null) {
            builder.append("\nData type: ").append(data.getClass().getSimpleName());
            if (data instanceof java.util.List) {
                builder.append(", Size: ").append (((java.util.List<?>) data).size());
            }
        }
        return builder.toString();
    }
}