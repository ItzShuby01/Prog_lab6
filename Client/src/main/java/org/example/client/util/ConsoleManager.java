package org.example.client.util;

import org.example.common.command.*;
import org.example.common.data.Person;
import org.example.common.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

interface ClientCommander {
    Response sendCommand(Command command);
}

public class ConsoleManager {
    private final IOService ioService;
    private final PersonIOService personIOService;
    private final ClientCommander clientCommander; // New dependency
    private boolean running = true;


    private final Map<String, BiFunction<String, Person, Command>> commandConstructors = new HashMap<>();

    public ConsoleManager(IOService ioService, PersonIOService personIOService, ClientCommander clientCommander) {
        this.ioService = ioService;
        this.personIOService = personIOService;
        this.clientCommander = clientCommander;
        initializeCommandConstructors();
    }

    private void initializeCommandConstructors() {
        // Commands without a Person object
        commandConstructors.put("help", (arg, p) -> new HelpCommand(arg));
        commandConstructors.put("info", (arg, p) -> new InfoCommand(arg));
        commandConstructors.put("show", (arg, p) -> new ShowCommand(arg));
        commandConstructors.put("clear", (arg, p) -> new ClearCommand(arg));
        commandConstructors.put("history", (arg, p) -> new HistoryCommand(arg));
        commandConstructors.put("max_by_id", (arg, p) -> new MaxByIdCommand(arg));
        commandConstructors.put("average_of_height", (arg, p) -> new AverageOfHeightCommand(arg));
        // Commands requiring an argument
        commandConstructors.put("remove_by_id", (arg, p) -> new RemoveByIdCommand(arg));
        commandConstructors.put("count_by_location", (arg, p) -> new CountByLocationCommand(arg));
        commandConstructors.put("execute_script", (arg, p) -> new ExecuteScriptCommand(arg)); // Server handles script content

        // Commands requiring a Person object (like add, add_if_max, update, remove_lower)
        commandConstructors.put("add", (arg, p) -> new AddCommand(arg, p));
        commandConstructors.put("add_if_max", (arg, p) -> new AddIfMaxCommand(arg, p));
        commandConstructors.put("update", (arg, p) -> new UpdateCommand(arg, p));
        commandConstructors.put("remove_lower", (arg, p) -> new RemoveLowerCommand(arg, p));

        // Special local command
        commandConstructors.put("exit", (arg, p) -> null); // Handled locally, doesn't create a command object
    }

    public void interactiveMode() {
        ioService.print("WELCOME TO THE PERSON COLLECTION APP");
        ioService.print("Enter 'help' to see available commands, 'exit' to stop the program");

        while (running) {
            String fullCommand = ioService.readLine("Enter command> ").trim();
            if (fullCommand.isEmpty()) {
                continue;
            }

            String[] parts = fullCommand.split(" ", 2);
            String cmdName = parts[0].toLowerCase(); // Normalize command name
            String arg = parts.length > 1 ? parts[1] : "";

            if ("exit".equalsIgnoreCase(cmdName)) {
                ioService.print("Exiting client application.");
                running = false;
                break;
            }

            // Determine if command needs a Person object
            Person personData = null;
            if (cmdName.equals("add") || cmdName.equals("add_if_max") || cmdName.equals("remove_lower")) {
                personData = personIOService.readPerson();
                if (personData == null) { // User might cancel or input invalid data
                    ioService.print("Person data input cancelled or invalid. Command not sent.");
                    continue;
                }
            } else if (cmdName.equals("update")) {
                try {
                    // For update, the arg is the ID. We need to read Person data after that.
                    if (arg.isEmpty()) {
                        ioService.print("Error: Update command requires an ID argument.");
                        continue;
                    }
                    Integer.parseInt(arg); // Validate ID format
                    personData = personIOService.readPerson();
                    if (personData == null) {
                        ioService.print("Person data input cancelled or invalid. Update command not sent.");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    ioService.print("Error: Invalid ID format for update command. Please enter an integer ID.");
                    continue;
                }
            }

            // Create command object
            Command command = null;
            try {
                BiFunction<String, Person, Command> constructor = commandConstructors.get(cmdName);
                if (constructor == null) {
                    ioService.print("Command '" + cmdName + "' not found. Enter 'help' for assistance.");
                    continue;
                }
                command = constructor.apply(arg, personData);
            } catch (Exception e) {
                ioService.print("Error creating command: " + e.getMessage());
                continue;
            }

            // Send command and display response
            if (command != null) { // Ensure command was created (not exit)
                Response response = clientCommander.sendCommand(command);
                ioService.print(response.getMessage());
            }
        }
    }
}