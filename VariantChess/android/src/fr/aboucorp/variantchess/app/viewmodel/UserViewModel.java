package fr.aboucorp.variantchess.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import fr.aboucorp.variantchess.app.db.VariantChessDatabase;
import fr.aboucorp.variantchess.app.db.user.User;
import fr.aboucorp.variantchess.app.db.user.UserDao;

public class UserViewModel extends AndroidViewModel {
    private UserDao userDao;
    private MutableLiveData<User> connected = new MutableLiveData<>();
    public UserViewModel(@NonNull Application application) {
        super(application);
        this.userDao = VariantChessDatabase.getDatabase(application).userDao();
    }

    public LiveData<List<User>> getUsers(){
        return this.userDao.getAll();
    }

    public LiveData<User> getConnected() {
        return connected;
    }

    public void setConnected(User connected) {
        this.connected.postValue(connected);
    }
}
