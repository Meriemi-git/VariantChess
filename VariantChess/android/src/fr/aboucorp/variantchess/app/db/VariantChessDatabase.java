package fr.aboucorp.variantchess.app.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import fr.aboucorp.variantchess.app.db.user.User;
import fr.aboucorp.variantchess.app.db.user.UserDao;

@Database(entities = {User.class}, version = 1)
public abstract class VariantChessDatabase extends RoomDatabase {
    private static final String DB_NAME = "variantchess.db";
    private static VariantChessDatabase INSTANCE;
    public abstract UserDao userDao();

    public static VariantChessDatabase getDatabase(Context context){
        if (INSTANCE == null) {
            synchronized (VariantChessDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            VariantChessDatabase.class, DB_NAME)
                            // TODO Remove it before production release
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.d("VariantChessDatabase", "populating with data...");
                                    new PopulateDbAsync(INSTANCE).execute();
                                }
                            })
                            .build();
                }
            }
        }

        return INSTANCE;
    }


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final UserDao userDao;

        public PopulateDbAsync(VariantChessDatabase instance) {
            userDao = instance.userDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}
