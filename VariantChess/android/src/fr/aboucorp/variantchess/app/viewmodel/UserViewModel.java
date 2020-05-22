package fr.aboucorp.variantchess.app.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
