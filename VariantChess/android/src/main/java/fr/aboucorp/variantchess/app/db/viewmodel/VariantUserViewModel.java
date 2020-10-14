package fr.aboucorp.variantchess.app.db.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import fr.aboucorp.variantchess.app.db.entities.VariantUser;
import fr.aboucorp.variantchess.app.db.repositories.ChessUserRepository;

public class VariantUserViewModel extends AndroidViewModel {

    private ChessUserRepository chessUserRepository;

    private Observer onGetConnected;

    public VariantUserViewModel(@NonNull Application application) {
        super(application);
        this.chessUserRepository = new ChessUserRepository(application);
        this.onGetConnected = (Observer<VariantUser>) user -> {
            if (user != null) {
                user.isConnected = false;
                this.chessUserRepository.update(user);
            }
        };
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.chessUserRepository.getConnected().removeObserver(this.onGetConnected);
    }

    public void insert(VariantUser variantUser) {
        this.chessUserRepository.insert(variantUser);
    }

    public LiveData<VariantUser> getConnected() {
        return this.chessUserRepository.getConnected();
    }

    public void setConnected(VariantUser connected) {
        this.chessUserRepository.setConnected(connected);
    }

    public void disconnectUser() {
        this.chessUserRepository.disconnectConnectedUser();
    }

    public void updateUser(VariantUser connected) {
        this.chessUserRepository.update(connected);
    }

    public void disconnectUserWithAuthToken(String authToken) {
        this.chessUserRepository.disconnectUserWithAuthToken(authToken);
    }
}
