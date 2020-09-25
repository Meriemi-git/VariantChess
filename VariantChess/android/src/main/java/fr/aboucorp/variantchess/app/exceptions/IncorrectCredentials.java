package fr.aboucorp.variantchess.app.exceptions;

public class IncorrectCredentials extends AuthentificationException {
    public IncorrectCredentials(String message) {
        super(message);
    }
}
