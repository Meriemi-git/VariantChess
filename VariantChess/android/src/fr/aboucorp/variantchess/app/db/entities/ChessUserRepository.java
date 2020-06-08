package fr.aboucorp.variantchess.app.db.entities;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import fr.aboucorp.variantchess.app.db.VariantChessDatabase;
import fr.aboucorp.variantchess.app.db.dao.ChessUserDao;

public class ChessUserRepository {
    private ChessUserDao chessUserDao;
    private MutableLiveData<ChessUser> connected;

    public ChessUserRepository(Application application) {
        VariantChessDatabase db = VariantChessDatabase.getDatabase(application);
        this.chessUserDao = db.chessUserDao();
        this.connected = new MutableLiveData<ChessUser>(this.chessUserDao.getConnected().getValue());
    }

    MutableLiveData<ChessUser> getConnected() {
        return this.connected;
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
            });
        }
        this.connected.setValue(connecting);
    }

    void insert(ChessUser chessUser) {
        VariantChessDatabase.databaseWriteExecutor.execute(() -> {
            this.chessUserDao.insertAll(chessUser);
        });
    }
}
