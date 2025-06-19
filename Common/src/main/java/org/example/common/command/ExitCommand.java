package org.example.common.command;

import java.io.Serializable;

public class ExitCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    public ExitCommand(String arg) {
        super("exit", arg);
    }
}