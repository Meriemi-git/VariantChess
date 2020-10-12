package fr.aboucorp.variantchess.app.db.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.repositories.ChessUserRepository;

public class UserViewModel extends AndroidViewModel {

    private ChessUserRepository chessUserRepository;

    private Observer onGetConnected;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.chessUserRepository = new ChessUserRepository(application);
        this.onGetConnected = (Observer<ChessUser>) user -> {
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

    public void setConnected(ChessUser connected) {
        this.chessUserRepository.setConnected(connected);
    }

    public void insert(ChessUser chessUser) {
        this.chessUserRepository.insert(chessUser);
    }

    public LiveData<ChessUser> getConnected() {
        return this.chessUserRepository.getConnected();
    }

    public void disconnectUser() {
        this.chessUserRepository.disconnectConnectedUser();
    }

    public void updateUser(ChessUser connected) {
        this.chessUserRepository.update(connected);
    }

    public void disconnectUserWithAuthToken(String authToken) {
        this.chessUserRepository.disconnectUserWithAuthToken(authToken);
    }
}
