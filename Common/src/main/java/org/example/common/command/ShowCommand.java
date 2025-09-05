package org.example.common.command;

import java.io.Serializable;

public class ShowCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    public ShowCommand(String arg) {
        super("show", arg);
    }
}