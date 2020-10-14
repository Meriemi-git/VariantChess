package fr.aboucorp.variantchess.app.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.aboucorp.variantchess.app.db.dao.ChessUserDao;
import fr.aboucorp.variantchess.app.db.dao.GameRulesDao;
import fr.aboucorp.variantchess.app.db.entities.GameRules;
import fr.aboucorp.variantchess.app.db.entities.VariantUser;

@Database(entities = {VariantUser.class, GameRules.class}, version = 3)
public abstract class VariantChessDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile VariantChessDatabase INSTANCE;

    private static RoomDatabase.Callback addDataOnCreate = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                INSTANCE.gameRulesDao().deleteAll();
                GameRules classicGameRules = new GameRules();
                classicGameRules.name = "gamerules_name_classic";
                classicGameRules.icon = "ic_chess_qlt45";
                classicGameRules.description = "gamerules_description_classic";
                classicGameRules.balance = 5;
                classicGameRules.difficulty = 3;
                GameRules randomGameRules = new GameRules();
                randomGameRules.name = "gamerules_name_random";
                randomGameRules.icon = "ic_chess_qlt45";
                randomGameRules.description = "gamerules_description_random";
                randomGameRules.balance = 1;
                randomGameRules.difficulty = 5;
                INSTANCE.gameRulesDao().insertAll(classicGameRules, randomGameRules);
            });
        }
    };

    public abstract ChessUserDao chessUserDao();

    public abstract GameRulesDao gameRulesDao();


    public static VariantChessDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VariantChessDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            VariantChessDatabase.class, "variantchess.db")
                            .addCallback(addDataOnCreate)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
