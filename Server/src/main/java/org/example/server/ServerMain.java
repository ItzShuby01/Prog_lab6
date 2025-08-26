package org.example.server;

import org.example.server.manager.CollectionManager;
import org.example.server.manager.FileManager;
import org.example.server.manager.ServerCommandManager;
import org.example.server.network.UDPServer;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {

        System.out.println("Starting server application...");

        String filePath = System.getenv("COLLECTION_FILE_PATH");
        CollectionManager collectionManager = new CollectionManager();
        FileManager fileManager = new FileManager(collectionManager);

        final int serverPort;
        try {
            serverPort = Integer.parseInt(args[0]);
            if (serverPort <= 0 || serverPort > 65535) {
                throw new IllegalArgumentException("Port number must be between 1 and 65535.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number format: " + args[0]);
            return;
        } catch (IllegalArgumentException e) {
            System.err.println("Port error: " + e.getMessage());
            return;
        }
        try {
            fileManager.loadCollectionFromXml(filePath);
            System.out.println("Collection loaded successfully.");
        } catch (Exception e) {
            System.err.println("Error loading collection: " + e.getMessage());
        }

        // Initialize the managers and Server network
        ServerCommandManager commandManager = new ServerCommandManager(collectionManager, fileManager);
        UDPServer server = new UDPServer(serverPort, commandManager);

        // Add a shutdown hook to save data and stop the server gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server shutdown hook activated.");
            // Save data before stopping
            try {
                commandManager.internalSave();
                System.out.println("Collection successfully saved on shutdown.");
            } catch (Exception e) {
                System.err.println("Error saving collection on shutdown: " + e.getMessage());
            } finally {
                server.stop(); // Stop the UDP server resources
            }
        }));

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Error starting or running server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}