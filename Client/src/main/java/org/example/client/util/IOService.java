package org.example.client.util;

public interface IOService extends AutoCloseable {
    void print(String message);

    String readLine(String prompt);

    int readInt(String prompt);

    double readDouble(String prompt);

    @Override
    void close();
}
