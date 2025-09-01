package org.example.client.util;

import org.example.client.network.UDPClient;
import org.example.common.command.*;
import org.example.common.data.Person;
import org.example.common.response.Response;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandParser {

    private final IOService ioService;
    private final PersonIOService personIOService;
    private final UDPClient client;
    private final Set<String> executingScripts = new HashSet<>(); //For recursion detection

    public CommandParser(IOService ioService, UDPClient client) {
        this.ioService = ioService;
        this.client = client;
        this.personIOService = new PersonIOService(ioService);
    }


    public void runLocalCommand(Command command) throws IOException {
        if(command instanceof ExecuteScriptCommand) {
            executeScript((ExecuteScriptCommand) command);
        }else if (command instanceof ExitCommand){
            ioService.print("Exiting client application! Goodbye");
            System.exit(0);
        }
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

    private void executeScript(ExecuteScriptCommand command) throws IOException {
        String filePath = command.getArg();
        ioService.print("Executing script from file: " + filePath);

        if (executingScripts.contains(filePath)) {
            ioService.print("Error: Recursive script execution detected for: " + filePath);
            return;
        }

        try {
            executingScripts.add(filePath);
            List<String> scriptLines = Files.readAllLines(Path.of(filePath));
            int lineIndex = 0;

            while(lineIndex < scriptLines.size()){
                String line = scriptLines.get(lineIndex++).trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+", 2);
                String commandName = parts[0].toLowerCase();
                String arg = parts.length > 1 ? parts[1].trim() : "";

                ioService.print("--- Executing command from script: " + line);

                Command cmdFromScript;
                try {
                    switch (commandName) {
                        case "add":
                        case "add_if_max":
                        case "update":
                        case "remove_lower":
                            // Read the next 10 lines for the Person object
                            if (lineIndex + 10 > scriptLines.size()) {
                                throw new IllegalArgumentException("Script ended unexpectedly. Missing person details.");
                            }
                            List<String> personData = scriptLines.subList(lineIndex, lineIndex + 10);
                            Person person = PersonBuilder.buildFromScript(personData);
                            lineIndex += 10;

                            // Add / AddIfMax / Update / RemoveLower can take inputs from a script file
                            switch (commandName) {
                                case "add":
                                    cmdFromScript = new AddCommand(arg, person);
                                    break;
                                case "add_if_max":
                                    cmdFromScript = new AddIfMaxCommand(arg, person);
                                    break;
                                case "update":
                                    cmdFromScript = new UpdateCommand(arg, person);
                                    break;
                                case "remove_lower":
                                    cmdFromScript = new RemoveLowerCommand(arg, person);
                                    break;
                                default:
                                    throw new IllegalStateException("Invalid command state.");
                            }
                            break;
                        case "exit":
                            cmdFromScript = new ExitCommand(arg);
                            break;

                        default:
                            // For all other commands, parse as a single line
                            cmdFromScript = parseCommand(line);
                            break;
                    }

                    if (cmdFromScript instanceof ExecuteScriptCommand || cmdFromScript instanceof ExitCommand) {
                        runLocalCommand(cmdFromScript);
                    } else {
                        Response response = client.sendAndReceive(cmdFromScript);
                        ioService.print("Server Response for '" + line + "': " + response.getMessage());
                    }
                } catch (Exception e) {
                    ioService.print("Error in script at line " + lineIndex + ": " + e.getMessage());
                }
            }
        } finally {
            executingScripts.remove(filePath);
        }
    }
}
