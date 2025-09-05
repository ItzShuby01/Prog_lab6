package org.example.common.command;

import java.io.Serializable;

public class CountByLocationCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;
    // The 'arg' field in the base Command class will hold the location string.

    public CountByLocationCommand(String arg) {
        super("count_by_location", arg);
    }
}