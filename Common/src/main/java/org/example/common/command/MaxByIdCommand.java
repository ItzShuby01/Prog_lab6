package org.example.common.command;

import java.io.Serializable;

public class MaxByIdCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    public MaxByIdCommand(String arg) {
        super("max_by_id", arg);
    }
}