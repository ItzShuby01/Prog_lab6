package org.example.client.network;

import org.example.common.command.Command;
import org.example.common.response.Response;
import org.example.common.util.SerializationUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;

public class UDPClient {
    private static final int BUFFER_SIZE = 65536; // Max UDP packet size
    private static final int MAX_RETRIES = 5; // How many times to retry connecting to the server
    private static final int RETRY_DELAY_MILLIS = 1000; // Delay between retries in milliseconds
    private static final int SOCKET_TIMEOUT_MILLIS = 5000; // Timeout for receiving a response from server

    private DatagramSocket clientSocket;
    private InetAddress serverAddress;
    private int serverPort;

    public UDPClient(String serverHost, int serverPort) throws IOException {
        this.serverAddress = InetAddress.getByName(serverHost);
        this.serverPort = serverPort;
        this.clientSocket = new DatagramSocket();
        this.clientSocket.setSoTimeout(SOCKET_TIMEOUT_MILLIS); // Set a timeout for receive operations
    }

    public Response sendAndReceive(Command command) throws IOException, ClassNotFoundException {
        byte[] sendData = SerializationUtil.serialize(command); // Serialize the command

        // Prepare the packet to send
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);

        byte[] receiveData = new byte[BUFFER_SIZE]; // Buffer for incoming response
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        for (int retry = 0; retry < MAX_RETRIES; retry++) {
            try {
                clientSocket.send(sendPacket); // Send the command
                System.out.println("Sent command: " + command.getName() + " to " + serverAddress + ":" + serverPort);

                // Wait for the response
                clientSocket.receive(receivePacket);
                System.out.println("Received response from server.");

                // Deserialize and return the response
                return (Response) SerializationUtil.deserialize(
                        java.util.Arrays.copyOf(receivePacket.getData(), receivePacket.getLength())
                );

            } catch (SocketTimeoutException e) {
                System.err.println("Server did not respond within " + SOCKET_TIMEOUT_MILLIS + "ms.");
            } catch (PortUnreachableException e) {
                System.err.println("Server port " + serverPort + " is unreachable.");
            } catch (IOException e) {
                System.err.println("Network error during communication: " + e.getMessage());
            }

            if (retry < MAX_RETRIES - 1) {
                System.out.println("Retrying... (" + (retry + 1) + "/" + MAX_RETRIES + ")");
                try {
                    Thread.sleep(RETRY_DELAY_MILLIS); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Client interrupted during retry delay.", ie);
                }
            }
        }
        throw new IOException("Failed to communicate with server after " + MAX_RETRIES + " retries. Server might be unavailable.");
    }

    public void close() {
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
            System.out.println("Client socket closed.");
        }
    }
}