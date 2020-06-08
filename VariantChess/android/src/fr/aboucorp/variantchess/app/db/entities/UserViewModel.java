package fr.aboucorp.variantchess.app.db.entities;

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UserViewModel extends AndroidViewModel {

    private ChessUserRepository chessUserRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.chessUserRepository = new ChessUserRepository(application);
    }


    public LiveData<ChessUser> getConnected() {
        return this.chessUserRepository.getConnected();
    }

    public void setConnected(ChessUser connected) {
        this.chessUserRepository.setConnected(connected);
    }

    public void insert(ChessUser chessUser) {
        this.chessUserRepository.insert(chessUser);
    }
}
