package org.example.server.network;

import org.example.common.command.Command;
import org.example.common.response.Response;
import org.example.common.util.SerializationUtil;
import org.example.server.manager.ServerCommandManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class UDPServer {
    private static final int BUFFER_SIZE = 65565;
    private static final long RESPONSE_TIMEOUT_MILLIS = 5000;

    private final int port;
    private final ServerCommandManager commandManager;
    private DatagramChannel channel;
    private Selector selector;
    private ByteBuffer buffer;
    private ExecutorService commandExecutorPool;

    private volatile boolean running; // This Flag  control the server's main loop

    public UDPServer(int port, ServerCommandManager commandManager) {
        this.port = port;
        this.commandManager = commandManager;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.commandExecutorPool = Executors.newFixedThreadPool(10);
        this.running = true;
    }

    public void start() throws IOException {
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(port));
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        System.out.println("Server started on UDP port " + port);

        while (running) {
            try {
                selector.select();
                if (!running) {
                    System.out.println("Server running flag set to false. Exiting main loop.");
                    break;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isReadable()) {
                        handleRead(key);
                    }
                }
            } catch (java.nio.channels.ClosedByInterruptException e) {
                System.out.println("Server selector was interrupted. Shutting down gracefully.");
                break; // Exit the loop gracefully
            } catch (java.nio.channels.ClosedSelectorException e) {
                if (!running) {
                    System.out.println("Selector closed during shutdown process.");
                    break;
                }
                System.err.println("Unexpected ClosedSelectorException during server operation: " + e.getMessage());
                e.printStackTrace();
                break;
            } catch (IOException e) {
                if (!running) {
                    System.out.println("IO Exception during server shutdown: " + e.getMessage());
                    break;
                }
                System.err.println("Server IOException during operation: " + e.getMessage());
                e.printStackTrace();
                break;
            } catch (Exception e) {
                System.err.println("Server unexpected error in main loop: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
        System.out.println("Server main loop stopped.");
    }

    private void handleRead(SelectionKey key) throws IOException {
        DatagramChannel currentChannel = (DatagramChannel) key.channel();
        buffer.clear();
        SocketAddress clientAddress = currentChannel.receive(buffer);

        if (clientAddress == null) {
            return;
        }

        buffer.flip();
        byte[] receivedBytes = new byte[buffer.remaining()];
        buffer.get(receivedBytes);

        System.out.println("Received " + receivedBytes.length + " bytes from " + clientAddress);

        Future<Response> futureResponse = commandExecutorPool.submit(() -> {
            try {
                Command commandDto = (Command) SerializationUtil.deserialize(receivedBytes);
                System.out.println("Executing command: " + commandDto.getName());
                return commandManager.executeCommand(commandDto);
            } catch (Exception e) {
                System.err.println("Error processing command from " + clientAddress + ": " + e.getMessage());
                e.printStackTrace();
                return new Response("Server error: " + e.getMessage(), false);
            }
        });

        try {
            Response response = futureResponse.get(RESPONSE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            sendResponse(response, clientAddress, currentChannel);
        } catch (java.util.concurrent.TimeoutException e) {
            System.err.println("Command processing timed out for " + clientAddress);
            sendResponse(new Response("Server response timeout.", false), clientAddress, currentChannel);
        } catch (Exception e) {
            System.err.println("Error getting command execution result or sending response to " + clientAddress + ": " + e.getMessage());
            e.printStackTrace();
            sendResponse(new Response("Server internal error.", false), clientAddress, currentChannel);
        }
    }

    private void sendResponse(Response response, SocketAddress clientAddress, DatagramChannel channelToSend) throws IOException {
        try {
            byte[] responseBytes = SerializationUtil.serialize(response);
            ByteBuffer responseBuffer = ByteBuffer.wrap(responseBytes);

            int bytesSent = channelToSend.send(responseBuffer, clientAddress);
            System.out.println("Sent " + bytesSent + " bytes to " + clientAddress);
        } catch (IOException e) {
            System.err.println("Error sending response to " + clientAddress + ": " + e.getMessage());
            throw e;
        }
    }

    public void stop() {
        this.running = false;
        System.out.println("Server shutting down...");

        try {
            if (selector != null) {
                selector.wakeup();
                selector.close();
                System.out.println("Selector closed.");
            }
            if (channel != null) {
                channel.close(); // Close the channel
                System.out.println("Channel closed.");
            }
            if (commandExecutorPool != null) {
                commandExecutorPool.shutdown();
                System.out.println("Awaiting termination of command executor pool...");
                if (!commandExecutorPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Command executor pool did not terminate gracefully. Forcibly shutting down.");
                    commandExecutorPool.shutdownNow(); // Force shutdown if it doesn't terminate in time
                } else {
                    System.out.println("Command executor pool terminated.");
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during server shutdown: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Server stopped.");
    }
}