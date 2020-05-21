package fr.aboucorp.variantchess.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.heroiclabs.nakama.api.User;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private MutableLiveData<User> connected = new MutableLiveData<>();
    public UserViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<User>> getUsers(){
        return null;
    }

    public LiveData<User> getConnected() {
        return connected;
    }

    public void setConnected(User connected) {
        this.connected.postValue(connected);
    }
}
