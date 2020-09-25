package fr.aboucorp.variantchess.app.exceptions;

public class MailAlreadyRegistered extends AuthentificationException {
    public MailAlreadyRegistered(String message) {
        super(message);
    }
}
