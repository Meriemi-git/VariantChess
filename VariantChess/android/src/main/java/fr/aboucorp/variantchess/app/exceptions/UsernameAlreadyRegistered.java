package fr.aboucorp.variantchess.app.exceptions;


public class UsernameAlreadyRegistered extends AuthentificationException {
    public UsernameAlreadyRegistered(String message) {
        super(message);
    }
}
