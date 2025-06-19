package org.example.server.commands;

import org.example.common.command.*;
import org.example.common.data.Coordinates;
import org.example.common.data.Country;
import org.example.common.data.EyeColor;
import org.example.common.data.HairColor;
import org.example.common.data.Location;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.common.util.ValidationUtil;
import org.example.server.manager.CollectionManager;
import org.example.server.manager.FileManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


public class ExecuteScript implements ServerCommand {
    public static final String DESCRIPTION = "execute_script file_name: read and execute the script from the specified file.";

    private final FileManager fileManager;
    private final CollectionManager collectionManager;
    private final Set<String> executingScripts = new HashSet<>(); // To detect recursion

    public ExecuteScript(FileManager fileManager, CollectionManager collectionManager) {
        this.fileManager = fileManager;
        this.collectionManager = collectionManager;
    }

    //Executes the 'execute_script' command on the server.
    public Response execute(ExecuteScriptCommand commandDto, CommandExecutable serverCommandExecutor) {
        String filePath = commandDto.getArg();

        if (filePath == null || filePath.isEmpty()) {
            return new Response("Error: Script file path is missing.", false);
        }

        if (executingScripts.contains(filePath)) {
            return new Response("Error: Recursive script execution detected for: " + filePath, false);
        }

        List<String> scriptLines = fileManager.readScript(filePath);
        if (scriptLines.isEmpty()) {
            return new Response("Script file is empty or could not be read: " + filePath, false);
        }

        executingScripts.add(filePath);
        StringBuilder executionLog = new StringBuilder(); // To collect messages
        boolean overallSuccess = true;

        try {
            int lineNum = 0;
            while (lineNum < scriptLines.size()) {
                String line = scriptLines.get(lineNum).trim();
                lineNum++;

                if (line.isEmpty() || line.startsWith("#")) { // Skip empty lines and comments
                    continue;
                }

                String[] parts = line.split(" ", 2);
                String cmdName = parts[0].toLowerCase();
                String arg = parts.length > 1 ? parts[1] : "";

                Command executableCommand = null;
                Response internalResponse = null;

                try {
                    switch (cmdName) {
                        case "add":
                        case "add_if_max":
                        case "update":
                        case "remove_lower":
                            int requiredPersonLines = ValidationUtil.INPUTS_LABELS.length;
                            List<String> personDataParts = new ArrayList<>();
                            for (int i = 0; i < requiredPersonLines && lineNum < scriptLines.size(); i++) {
                                personDataParts.add(scriptLines.get(lineNum).trim());
                                lineNum++;
                            }

                            Person personFromScript = buildPersonFromScript(personDataParts.toArray(new String[0]));
                            if (personFromScript == null) {
                                internalResponse = new Response("Error building person from script at line " + (lineNum - requiredPersonLines) + ": Invalid data.", false);
                                overallSuccess = false;
                                break; // Skip to next command
                            }

                            if (cmdName.equals("add")) {
                                executableCommand = new AddCommand(arg, personFromScript);
                            } else if (cmdName.equals("add_if_max")) {
                                executableCommand = new AddIfMaxCommand(arg, personFromScript);
                            } else if (cmdName.equals("update")) {
                                executableCommand = new UpdateCommand(arg, personFromScript);
                            } else if (cmdName.equals("remove_lower")) {
                                executableCommand = new RemoveLowerCommand(arg, personFromScript);
                            }
                            break;

                        case "remove_by_id":
                            executableCommand = new RemoveByIdCommand(arg);
                            break;
                        case "count_by_location":
                            executableCommand = new CountByLocationCommand(arg);
                            break;
                        case "execute_script":
                            executableCommand = new ExecuteScriptCommand(arg);
                            break;
                        case "help":
                            executableCommand = new HelpCommand(arg);
                            break;
                        case "info":
                            executableCommand = new InfoCommand(arg);
                            break;
                        case "show":
                            executableCommand = new ShowCommand(arg);
                            break;
                        case "save":
                            // Save is server-only and not callable by client scripts.
                            // If it appears in a script, it's an error from the script perspective.
                            executionLog.append("Warning: 'save' command ignored in script.\n");
                            continue; // Skip this command
                        case "clear":
                            executableCommand = new ClearCommand(arg);
                            break;
                        case "history":
                            executableCommand = new HistoryCommand(arg);
                            break;
                        case "max_by_id":
                            executableCommand = new MaxByIdCommand(arg);
                            break;
                        case "average_of_height":
                            executableCommand = new AverageOfHeightCommand(arg);
                            break;
                        case "exit":
                            // Client-side command, server will not execute this.
                            executionLog.append("Warning: 'exit' command ignored in script.\n");
                            continue;
                        default:
                            executionLog.append("Error: Unknown command in script: ").append(cmdName).append(" at line ").append(lineNum).append("\n");
                            overallSuccess = false;
                            continue; // Skip to next command
                    }

                    if (executableCommand != null) {
                        internalResponse = serverCommandExecutor.executeCommand(executableCommand);
                        executionLog.append("  Command '").append(cmdName).append("' (").append(lineNum).append("): ").append(internalResponse.getMessage()).append("\n");
                        if (!internalResponse.isSuccess()) {
                            overallSuccess = false; // Mark overall script as failed if any command fails
                        }
                    }

                } catch (Exception e) {
                    executionLog.append("  Error executing '").append(cmdName).append("' at line ").append(lineNum).append(": ").append(e.getMessage()).append("\n");
                    overallSuccess = false;
                }
            }
        } finally {
            executingScripts.remove(filePath);
        }

        if (overallSuccess) {
            return new Response("Script '" + filePath + "' executed successfully.\n" + executionLog.toString(), true);
        } else {
            return new Response("Script '" + filePath + "' executed with errors.\n" + executionLog.toString(), false);
        }
    }

    // Helper method to build a Person object from an array of script parts.
    private Person buildPersonFromScript(String[] parts) {
        if (parts.length < ValidationUtil.INPUTS_LABELS.length) {
            return null;
        }

        try {
            String name = ValidationUtil.validateString(parts[0], ValidationUtil.INPUTS_LABELS[0]);
            int x = ValidationUtil.validateInt(parts[1], ValidationUtil.INPUTS_LABELS[1]);
            double y = ValidationUtil.validateDouble(parts[2], ValidationUtil.INPUTS_LABELS[2]);
            Coordinates coordinates = new Coordinates(x, y);

            double height = ValidationUtil.validateDouble(parts[3], ValidationUtil.INPUTS_LABELS[3]);
            EyeColor eyeColor = EyeColor.valueOf(parts[4].toUpperCase());
            HairColor hairColor = HairColor.valueOf(parts[5].toUpperCase());

            Country nationality = null;
            if (!parts[6].trim().isEmpty()) { // Nationality is optional
                nationality = Country.valueOf(parts[6].toUpperCase());
            }

            Location location = null;
            // Check if location data is present
            if (parts.length > 9 && !Stream.of(parts[7], parts[8], parts[9]).allMatch(String::isBlank)) {
                float locX = ValidationUtil.validateFloat(parts[7], ValidationUtil.INPUTS_LABELS[7]);
                float locY = ValidationUtil.validateFloat(parts[8], ValidationUtil.INPUTS_LABELS[8]);
                String locName = ValidationUtil.validateString(parts[9], ValidationUtil.INPUTS_LABELS[9]);
                location = new Location(locX, locY, locName);
            }

            // ID and creationDate will be set by the server's Add command
            return new Person(null, name, coordinates, LocalDateTime.now(), height, eyeColor, hairColor, nationality, location);

        } catch (IllegalArgumentException e) {
            System.err.println("Script Person data validation error: " + e.getMessage());
            return null;
        }
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}