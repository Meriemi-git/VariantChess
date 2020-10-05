package fr.aboucorp.variantchess.app.db.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import fr.aboucorp.variantchess.app.db.VariantChessDatabase;
import fr.aboucorp.variantchess.app.db.dao.GameRulesDao;
import fr.aboucorp.variantchess.app.db.entities.GameRules;

public class GameRulesRepository {
    private GameRulesDao gameRulesDao;

    public GameRulesRepository(Application application) {
        VariantChessDatabase db = VariantChessDatabase.getDatabase(application);
        this.gameRulesDao = db.gameRulesDao();
    }

    @NonNull
    public LiveData<List<GameRules>> getAllGameRules() {
        return this.gameRulesDao.getAll();
    }

}
