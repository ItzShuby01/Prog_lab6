package org.example.client;

import org.example.client.network.UDPClient;
import org.example.client.util.CommandParser;
import org.example.client.util.ConsoleIOService;
import org.example.client.util.IOService;
import org.example.common.command.Command;
import org.example.common.command.ExitCommand;
import org.example.common.response.Response;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
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

        IOService ioService = null;
        UDPClient client = null;
        try {
            ioService = new ConsoleIOService(new Scanner(System.in));
            CommandParser commandParser = new CommandParser(ioService);
            client = new UDPClient(serverHost, serverPort);

            ioService.print("Client started. Connecting to server " + serverHost + ":" + serverPort);
            ioService.print("Type 'help' for available commands or 'exit' to quit.");

            while (true) {
                String line = ioService.readLine("> ");

                if (line == null || line.trim().isEmpty()) {
                    continue; // Skip empty input
                }

                Command command = null;
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

                Response response = null;
                try {
                    response = client.sendAndReceive(command);
                    if (response != null) {
                        if (response.isSuccess()) {
                            ioService.print("Server Response: " + response.getMessage());
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
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.err.println("Error initializing client: " + e.getMessage());
        } finally {
            if (ioService != null) {
                ioService.close(); // Close Scanner
            }
            if (client != null) {
                client.close(); // Close UDP socket
            }
        }
    }
}