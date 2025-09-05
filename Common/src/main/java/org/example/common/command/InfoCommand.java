package org.example.common.command;

import java.io.Serializable;

public class InfoCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    public InfoCommand(String arg) {
        super("info", arg);
    }
}