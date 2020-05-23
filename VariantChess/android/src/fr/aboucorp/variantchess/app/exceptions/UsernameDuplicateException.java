package fr.aboucorp.variantchess.app.exceptions;


public class UsernameDuplicateException extends Throwable {
    public UsernameDuplicateException(String message) {
        super(message);
    }
}
