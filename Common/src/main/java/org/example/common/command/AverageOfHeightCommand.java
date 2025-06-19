package org.example.common.command;

import java.io.Serializable;

public class AverageOfHeightCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    public AverageOfHeightCommand(String arg) {
        super("average_of_height", arg);
    }
}