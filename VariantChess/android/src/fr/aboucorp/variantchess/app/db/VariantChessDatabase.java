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
import fr.aboucorp.variantchess.app.db.entities.ChessUser;

@Database(entities = {ChessUser.class}, version = 1)
public abstract class VariantChessDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile VariantChessDatabase INSTANCE;

    private static RoomDatabase.Callback addDataOnCreate = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // TODO populate database
            });
        }
    };

    public abstract ChessUserDao chessUserDao();

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
