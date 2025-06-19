package org.example.server.commands;

import org.example.common.command.Command;
import org.example.common.response.Response;

public interface CommandExecutable {
    // Executes a given Command DTO and returns a Response DTO.
    Response executeCommand(Command commandDto);
}