package org.example.common.command;

import java.io.Serializable;

public class ExecuteScriptCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;
    // The 'arg' field in the base Command class will hold the file path.

    public ExecuteScriptCommand(String arg) {
        super("execute_script", arg);
    }
}