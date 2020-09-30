package fr.aboucorp.variantchess.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.entities.ChessUserRepository;

public class UserViewModel extends AndroidViewModel {

    private ChessUserRepository chessUserRepository;
    private LiveData<ChessUser> connected;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.chessUserRepository = new ChessUserRepository(application);
        this.connected = this.chessUserRepository.getConnected();
    }

    public LiveData<ChessUser> getConnected() {
        return this.connected;
    }

    public void setConnected(ChessUser connected) {
        this.chessUserRepository.setConnected(connected);
    }

    public void insert(ChessUser chessUser) {
        this.chessUserRepository.insert(chessUser);
    }

    public void disconnectUser() {
        this.chessUserRepository.disconnect();
    }
}
