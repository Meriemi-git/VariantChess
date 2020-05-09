package fr.aboucorp.variantchess.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import fr.aboucorp.variantchess.app.db.VariantChessDatabase;
import fr.aboucorp.variantchess.app.db.user.User;
import fr.aboucorp.variantchess.app.db.user.UserDao;

public class UserViewModel extends AndroidViewModel {
    private UserDao userDao;
    private User connected;
    public UserViewModel(@NonNull Application application) {
        super(application);
        this.userDao = VariantChessDatabase.getDatabase(application).userDao();
    }

    public LiveData<List<User>> getUsers(){
        return this.userDao.getAll();
    }

    public User getConnectedUser(){
        return this.userDao.getConnectedUser().getValue();
    }

    public User getConnected() {
        return connected;
    }

    public void setConnected(User connected) {
        this.connected = connected;
    }
}
