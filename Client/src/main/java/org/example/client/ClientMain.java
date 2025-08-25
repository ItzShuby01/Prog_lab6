package org.example.client;

import org.example.client.network.UDPClient;
import org.example.client.util.CommandParser;
import org.example.client.util.ConsoleIOService;
import org.example.client.util.IOService;
import org.example.common.command.Command;
import org.example.common.command.ExecuteScriptCommand;
import org.example.common.command.HelpCommand;
import org.example.common.command.ShowCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Scanner;

public class ClientMain {
    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = 25565;

    public static void main(String[] args) {
        String serverHost = DEFAULT_SERVER_HOST;
        int serverPort = DEFAULT_SERVER_PORT;

        // Parse command line arguments for server host and port
        if (args.length == 2) {
            serverHost = args[0];
            try {
                serverPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number format. Using default port " + DEFAULT_SERVER_PORT);
            }
        } else if (args.length > 0) {
            System.err.println("Usage: java -jar client.jar [server_host] [server_port]");
            System.err.println("Using default server: " + DEFAULT_SERVER_HOST + ":" + DEFAULT_SERVER_PORT);
        }

        UDPClient client;
        try(IOService ioService= new ConsoleIOService(new Scanner(System.in))) {
            client = new UDPClient(serverHost, serverPort);
            CommandParser commandParser = new CommandParser(ioService,client);


            ioService.print("Client started. Connecting to server " + serverHost + ":" + serverPort);
            ioService.print("Type 'help' for available commands or 'exit' to quit.");

            while (true) {
                String line = ioService.readLine("> ");

                if (line == null || line.trim().isEmpty()) {
                    continue; // Skip empty input
                }

                Command command ;
                try {
                    if (line.trim().toLowerCase().startsWith("exit")) {
                        ioService.print("Exiting client application. Goodbye!");
                        break;
                    }
                    if (line.trim().toLowerCase().startsWith("execute_script")) {
                        String filePath = line.trim().split("\\s+", 2).length > 1 ? line.trim().split("\\s+", 2)[1] : "";
                        if (filePath.isEmpty()) {
                            ioService.print("Client Command Error: execute_script requires a file path.");
                            continue;
                        }
                        commandParser.runLocalCommand(new ExecuteScriptCommand(filePath));
                        continue;
                    }

                    // For all other commands, parse and send to server
                    command = commandParser.parseCommand(line);
                } catch (IllegalArgumentException | ClassNotFoundException e) {
                    ioService.print("Client Command Error: " + e.getMessage());
                    continue;
                } catch (IOException e) {
                    ioService.print("Input/Output error: " + e.getMessage());
                    continue;
                }

                // If the command is null (e.g., if a local command was handled), skip the rest
                if (command == null) {
                    continue;
                }

                try {
                    Response response  = client.sendAndReceive(command);
                    if (response != null) {
                        if (response.isSuccess()) {
                            if(command instanceof HelpCommand){
                                String helpMessage = "Client-side commands:\n"
                                        + "  " + HelpCommand.EXECUTE_SCRIPT_DESCRIPTION + "\n"
                                        + "  exit: close the application\n"
                                        + "\n"
                                        + "Server-side commands:\n"
                                        + response.getMessage(); // This is the help message from the server
                                ioService.print(helpMessage);
                            } else {
                                // For all other commands, print the server's message normally
                            ioService.print("Server Response: " + response.getMessage());
                            }

                            // --- START OF THE ADDED BLOCK FOR SHOW COMMAND DATA ---
                            if (command instanceof ShowCommand && response.getData() != null) {
                                if (response.getData() instanceof List) {
                                    List<?> dataList = (List<?>) response.getData();
                                    if (dataList.isEmpty()) {
                                        ioService.print("The collection is empty.");
                                    } else {
                                        ioService.print("--- Collection Data ---");
                                        for (Object item : dataList) {
                                            if (item instanceof Person) {
                                                ioService.print(((Person) item).toString()); // Print Person details
                                            } else {
                                                ioService.print(item.toString()); // Fallback for other types
                                            }
                                        }
                                        ioService.print("--- End Collection Data ---");
                                    }
                                } else {
                                    // This case indicates an unexpected data type if it's a ShowCommand
                                    ioService.print("Received unexpected data type for ShowCommand: " + response.getData().getClass().getName());
                                }
                            }
                            // --- END OF THE ADDED BLOCK FOR SHOW COMMAND DATA ---

                        } else {
                            ioService.print("Server Error: " + response.getMessage());
                        }
                    } else {
                        ioService.print("Received null response from server.");
                    }
                } catch (SocketTimeoutException e) {
                    ioService.print("Server timed out. " + e.getMessage());
                } catch (PortUnreachableException e) {
                    ioService.print("Server port unreachable. " + e.getMessage());
                } catch (IOException e) {
                    ioService.print("Network communication error: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    ioService.print("Received unknown object from server: " + e.getMessage());
                } catch (Exception e) {
                    ioService.print("An unexpected error occurred: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Error initializing client: " + e.getMessage());
        }
    }
}