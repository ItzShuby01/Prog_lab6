package org.example.client.util;

import java.util.Scanner;

public class ConsoleIOService implements IOService {
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleIOService(Scanner scanner) {
    }

    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public String readLine(String prompt) {
        print(prompt);
        return scanner.nextLine();
    }

    @Override
    public int readInt(String prompt) {
        int number = 0;
        boolean validInput = false;

        do {
            print(prompt);
            String input = scanner.nextLine();

            try {
                number = Integer.parseInt(input);
                validInput = true; // Exit condition
            } catch (NumberFormatException e) {
                print("Invalid input. Please enter a valid integer.");
            }
        } while (!validInput);

        return number;
    }

    @Override
    public double readDouble(String prompt) {
        double number = 0;
        boolean validInput = false;

        do {
            print(prompt);
            String input = scanner.nextLine();
            try {
                number = Double.parseDouble(input);
                validInput = true;
            } catch (NumberFormatException e) {
                print("Invalid input. Please enter a valid double.");
            }
        } while (!validInput);

        return number;
    }


    @Override
    public void close() {
        scanner.close();
    }
}

