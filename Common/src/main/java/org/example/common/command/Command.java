package org.example.common.command;

import java.io.Serializable;


 /* Abstract base class for all commands sent from the client to the server.
 All concrete command classes must extend this.
 */

public abstract class Command implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String arg;

    public Command(String name, String arg) {
        this.name = name;
        this.arg = arg;
    }

    public Command(String name) {
        this(name, ""); // No argument
    }

    public String getName() {
        return name;
    }

    public String getArg() {
        return arg;
    }

    public String getDescription() {
        return "No description available.";
    }

    @Override
    public String toString() {
        return name + (arg.isEmpty() ? "" : " " + arg);
    }
}