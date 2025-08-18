package org.example.client;

import org.example.client.network.UDPClient;
import org.example.client.util.CommandParser;
import org.example.client.util.ConsoleIOService;
import org.example.client.util.IOService;
import org.example.common.command.Command;
import org.example.common.command.ExitCommand;
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
            CommandParser commandParser = new CommandParser(ioService);
            client = new UDPClient(serverHost, serverPort);

            ioService.print("Client started. Connecting to server " + serverHost + ":" + serverPort);
            ioService.print("Type 'help' for available commands or 'exit' to quit.");

            while (true) {
                String line = ioService.readLine("> ");

                if (line == null || line.trim().isEmpty()) {
                    continue; // Skip empty input
                }

                Command command ;
                try {
                    command = commandParser.parseCommand(line);
                } catch (IllegalArgumentException e) {
                    ioService.print("Client Command Error: " + e.getMessage());
                    continue;
                } catch (IOException e) {
                    ioService.print("Input/Output error: " + e.getMessage());
                    continue;
                }

                // Check if it's the client-side 'exit' command
                if (command instanceof ExitCommand) {
                    ioService.print("Exiting client application. Goodbye!");
                    break;
                }

                try {
                    Response response  = client.sendAndReceive(command);
                    if (response != null) {
                        if (response.isSuccess()) {
                            ioService.print("Server Response: " + response.getMessage());

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