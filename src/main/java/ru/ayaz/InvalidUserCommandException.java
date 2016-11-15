package ru.ayaz;

public class InvalidUserCommandException extends Exception {

    private String invalidCommand;
    private String invalidArgument;

    public InvalidUserCommandException(String invalidCommand, String invalidArgument) {
        this.invalidCommand = invalidCommand;
        this.invalidArgument = invalidArgument;
    }

    @Override
    public String toString() {
        return "Invalid command: " + invalidCommand + " with argument " + invalidArgument;
    }

}
