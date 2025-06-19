package org.example.common.command;

import java.io.Serializable;

public class RemoveByIdCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;
    // The 'arg' field in the base Command class will hold the ID string.

    public RemoveByIdCommand(String arg) {
        super("remove_by_id", arg);
    }
}