package org.example.client.util;

import org.example.common.command.*;
import org.example.common.data.Person;


import java.io.IOException;

public class CommandParser {

    private final IOService ioService;
    private final PersonIOService personIOService;

    public CommandParser(IOService ioService) {
        this.ioService = ioService;
        this.personIOService = new PersonIOService(ioService);
    }

    public Command parseCommand(String input) throws IOException {
        String[] parts = input.trim().split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1].trim() : "";

        switch (commandName) {
            case "help":
                return new HelpCommand(arg);
            case "info":
                return new InfoCommand(arg);
            case "show":
                return new ShowCommand(arg);
            case "add":
                ioService.print("--- Entering Person details for 'add' ---");
                Person person = personIOService.readPerson();
                return new AddCommand(arg, person);
            case "update":
                if (arg.isEmpty()) throw new IllegalArgumentException("Update command requires an ID.");
                ioService.print("--- Entering new Person details for 'update' (ID: " + arg + ") ---");
                Person updatedPerson = personIOService.readPerson();
                return new UpdateCommand(arg, updatedPerson);


            case "remove_by_id":
                if (arg.isEmpty()) throw new IllegalArgumentException("remove_by_id requires an ID.");
                return new RemoveByIdCommand(arg);

            case "clear":
                return new ClearCommand(arg);
            case "execute_script":
                if (arg.isEmpty()) throw new IllegalArgumentException("execute_script requires a file path.");
                return new ExecuteScriptCommand(arg);
            case "exit":
                return new ExitCommand(arg);
            case "add_if_max":
                ioService.print("--- Entering Person details for 'add_if_max' ---");
                Person addIfMaxPerson = personIOService.readPerson();
                return new AddIfMaxCommand(arg, addIfMaxPerson);
            case "remove_lower":
                ioService.print("--- Entering Person details for 'remove_lower' ---");
                Person removeLowerPerson = personIOService.readPerson();
                return new RemoveLowerCommand(arg, removeLowerPerson);
            case "history":
                return new HistoryCommand(arg);
            case "max_by_id":
                return new MaxByIdCommand(arg);
            case "average_of_height":
                return new AverageOfHeightCommand(arg);
            case "count_by_location":
                if (arg.isEmpty()) throw new IllegalArgumentException("count_by_location requires a location argument.");
                return new CountByLocationCommand(arg);
            default:
                throw new IllegalArgumentException("Unknown command: " + commandName);
        }
    }
}