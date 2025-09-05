package org.example.common.command;

import java.io.Serializable;

public class HelpCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String EXECUTE_SCRIPT_DESCRIPTION = "execute_script file_name: read and execute the script from the specified file.";

    public HelpCommand(String arg) {
        super("help", arg);
    }
}