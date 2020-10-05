package fr.aboucorp.variantchess.app.db.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import fr.aboucorp.variantchess.app.db.entities.GameRules;
import fr.aboucorp.variantchess.app.db.repositories.GameRulesRepository;

public class GameRulesViewModel extends AndroidViewModel {

    private GameRulesRepository mRepository;
    private LiveData<List<GameRules>> allGameRules;

    public GameRulesViewModel(Application application) {
        super(application);
        mRepository = new GameRulesRepository(application);
        allGameRules = mRepository.getAllGameRules();
    }

    public LiveData<List<GameRules>> getAllGameRules() {
        return allGameRules;
    }
}
