package org.example.common.command;

import java.io.Serializable;

public class HistoryCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;

    public HistoryCommand(String arg) {
        super("history", arg);
    }
}