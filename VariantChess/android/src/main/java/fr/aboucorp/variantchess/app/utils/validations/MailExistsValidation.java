package fr.aboucorp.variantchess.app.utils.validations;

import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;

import fr.aboucorp.variantchess.app.multiplayer.SessionManager;

public class MailExistsValidation implements SimpleCustomValidation {
    private final SessionManager sessionManager;
    public MailExistsValidation(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean compare(String mail) {
        return this.sessionManager.mailExists(mail);
    }
}
