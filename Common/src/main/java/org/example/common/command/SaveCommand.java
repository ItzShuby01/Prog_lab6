package org.example.common.command;

import java.io.Serializable;

public class SaveCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    public SaveCommand(String arg) {
        super("save", arg);
    }
}