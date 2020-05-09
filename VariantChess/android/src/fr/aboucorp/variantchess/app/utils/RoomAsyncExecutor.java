package fr.aboucorp.variantchess.app.utils;

import android.os.AsyncTask;

public abstract class RoomAsyncExecutor  extends AsyncTask<Void,Void,Void> {

    public abstract void doJob();

    @Override
    protected Void doInBackground(Void... voids) {
        doJob();
        return null;
    }
}
