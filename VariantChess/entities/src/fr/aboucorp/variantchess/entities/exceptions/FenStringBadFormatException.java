package fr.aboucorp.variantchess.entities.exceptions;

public class FenStringBadFormatException extends Exception {
    public FenStringBadFormatException(String message) {
        super(message);
    }

    public FenStringBadFormatException() {
    }
}
