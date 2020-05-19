package fr.aboucorp.variantchess.app.exception;

import androidx.annotation.Nullable;

public class UsernameDuplicateException extends Throwable {
    public UsernameDuplicateException(@Nullable String message) {
        super(message);
    }
}
