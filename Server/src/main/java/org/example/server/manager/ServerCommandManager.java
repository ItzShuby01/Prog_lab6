package org.example.server.manager;

import org.example.common.command.*;
import org.example.common.response.Response;
import org.example.server.commands.*;
import org.example.server.commands.CommandExecutable;

import java.util.*;
import java.util.function.Function;

// This class acts as the central dispatcher for server-side commands.
// It maps incoming Command DTOs to the correct server-side command logic.
public class ServerCommandManager implements CommandExecutable {
    private final CollectionManager collectionManager;
    private final FileManager fileManager;
    private final LinkedList<String> commandHistory = new LinkedList<>();
    private final Map<String, Function<Command, Response>> commandHandlers = new HashMap<>();

    // This map stores instances of our server-side command handlers for easy access to their descriptions and execution logic.
    private final Map<String, ServerCommand> commandInstances = new HashMap<>();

    public ServerCommandManager(
            CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
        initializeCommands();
    }

    private void initializeCommands() {
        // Instantiate ALL server-side command classes (org.example.server.commands.*)
        Add addCmd = new Add(collectionManager);
        AddIfMax addIfMaxCmd = new AddIfMax(collectionManager);
        Update updateCmd = new Update(collectionManager);
        AverageOfHeight averageOfHeightCmd = new AverageOfHeight(collectionManager);
        Clear clearCmd = new Clear(collectionManager);
        CountByLocation countByLocationCmd = new CountByLocation(collectionManager);
        History historyCmd = new History();
        Info infoCmd = new Info(collectionManager);
        MaxById maxByIdCmd = new MaxById(collectionManager);
        RemoveById removeByIdCmd = new RemoveById(collectionManager);
        RemoveLower removeLowerCmd = new RemoveLower(collectionManager);
        Save saveCmd = new Save(collectionManager, fileManager);
        Show showCmd = new Show(collectionManager);
        Help helpCmd = new Help(this);


        // Each lambda performs the necessary type casting and calls the specific command's execute method.
        registerServerCommand("add", cmd -> addCmd.execute((AddCommand) cmd), addCmd);
        registerServerCommand("add_if_max", cmd -> addIfMaxCmd.execute((AddIfMaxCommand) cmd), addIfMaxCmd);
        registerServerCommand("average_of_height", cmd -> averageOfHeightCmd.execute((AverageOfHeightCommand) cmd), averageOfHeightCmd);
        registerServerCommand("clear", cmd -> clearCmd.execute((ClearCommand) cmd), clearCmd);
        registerServerCommand("count_by_location", countByLocationCmd::execute, countByLocationCmd);
        registerServerCommand("help", cmd -> helpCmd.execute((HelpCommand) cmd), helpCmd);
        registerServerCommand("history", cmd -> historyCmd.execute((HistoryCommand) cmd, new ArrayList<>(commandHistory)), historyCmd);
        registerServerCommand("info", cmd -> infoCmd.execute((InfoCommand) cmd), infoCmd);
        registerServerCommand("max_by_id", cmd -> maxByIdCmd.execute((MaxByIdCommand) cmd), maxByIdCmd);
        registerServerCommand("remove_by_id", cmd -> removeByIdCmd.execute((RemoveByIdCommand) cmd), removeByIdCmd);
        registerServerCommand("remove_lower", cmd -> removeLowerCmd.execute((RemoveLowerCommand) cmd), removeLowerCmd);
        registerServerCommand("save", cmd -> saveCmd.execute((SaveCommand) cmd), saveCmd); // Server-side save
        registerServerCommand("show", cmd -> showCmd.execute((ShowCommand) cmd), showCmd);
        registerServerCommand("update", cmd -> updateCmd.execute((UpdateCommand) cmd), updateCmd);
    }

    // Helper method to register commands consistently
    private void registerServerCommand(String name, Function<Command, Response> handler, ServerCommand instance) {
        commandHandlers.put(name, handler);
        commandInstances.put(name, instance); // Store ServerCommand instance for getDescription()
    }

    // The core method for processing commands coming from the client (as DTOs)
    @Override
    public Response executeCommand(Command commandDto) {
        if (commandDto == null || commandDto.getName() == null) {
            return new Response("Received null command or command with no name.", false);
        }

        String commandName = commandDto.getName().toLowerCase();
        Function<Command, Response> handler = commandHandlers.get(commandName);

        if (handler != null) {
            addToHistory(commandName);
            try {
                return handler.apply(commandDto);
            } catch (ClassCastException e) {
                return new Response("Internal server error: Mismatched command DTO type for '" + commandName + "'. " + e.getMessage(), false);
            } catch (Exception e) {
                System.err.println("Error executing command '" + commandName + "': " + e.getMessage());
                return new Response("Server error during command execution: " + e.getMessage(), false);
            }
        } else {
            return new Response("Unknown command: " + commandName, false);
        }
    }

    // Commands Descriptions GETTER
    public Map<String, String> getCommandDescriptions() {
        Map<String, String> descriptions = new TreeMap<>();
        commandInstances.forEach((name, cmd) -> descriptions.put(name, cmd.getDescription()));
        return descriptions;
    }

    // Helper method for 'History'
    public void addToHistory(String cmdName) {
        if (commandHistory.size() == 7) commandHistory.removeFirst();
        commandHistory.add(cmdName);
    }

    // Method for server-internal save, not exposed to client commands
    public Response internalSave() {
        // This directly calls the save command's execute method with a dummy SaveCommand DTO
        return ((Save) commandInstances.get("save")).execute(new SaveCommand("internal"));
    }
}