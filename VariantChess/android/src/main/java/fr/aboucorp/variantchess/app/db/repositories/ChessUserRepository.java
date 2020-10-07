package fr.aboucorp.variantchess.app.db.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.aboucorp.variantchess.app.db.VariantChessDatabase;
import fr.aboucorp.variantchess.app.db.dao.ChessUserDao;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;

public class ChessUserRepository {

    private ChessUserDao chessUserDao;

    @NonNull
    private MutableLiveData<ChessUser> chessUser = new MutableLiveData<>();

    public ChessUserRepository(Application application) {
        VariantChessDatabase db = VariantChessDatabase.getDatabase(application);
        this.chessUserDao = db.chessUserDao();
    }

    @NonNull
    public LiveData<ChessUser> getConnected() {
        return this.chessUserDao.getConnected();
    }

    public void setConnected(ChessUser connecting) {
        if (connecting != null) {
            connecting.isConnected = true;
            VariantChessDatabase.databaseWriteExecutor.execute(() -> {
                ChessUser user = this.chessUserDao.findByName(connecting.username);
                if (user != null) {
                    user.isConnected = true;
                    this.chessUserDao.update(user);

                } else {
                    this.chessUserDao.insertAll(connecting);
                }
                this.chessUserDao.disconnectAllOthers(connecting.username);
            });
            this.chessUser.postValue(connecting);
        }
    }

    public void insert(ChessUser chessUser) {
        VariantChessDatabase.databaseWriteExecutor.execute(() -> {
            this.chessUserDao.insertAll(chessUser);
        });
    }

    public void update(ChessUser chessUser) {
        this.chessUserDao.update(chessUser);
    }

}
