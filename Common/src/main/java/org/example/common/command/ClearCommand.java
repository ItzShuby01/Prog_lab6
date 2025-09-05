package org.example.common.command;

import java.io.Serializable;

public class ClearCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    public ClearCommand(String arg) {
        super("clear", arg);
    }
}