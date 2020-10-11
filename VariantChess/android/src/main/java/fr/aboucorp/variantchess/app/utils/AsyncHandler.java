package fr.aboucorp.variantchess.app.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class AsyncHandler {

    /**
     * Start execute() on other thread and then execute callback on UI thread
     */
    public void start() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() ->
        {
            try {
                Object args = this.executeAsync();
                new Handler(Looper.getMainLooper()).post(() -> this.callbackOnUI(args));
            } catch (Exception ex) {
                new Handler(Looper.getMainLooper()).post(() -> this.error(ex));
            }
        });
    }

    protected abstract Object executeAsync() throws Exception;

    protected void callbackOnUI(Object arg) {
    }

    protected void error(Exception ex) {
        Log.e("fr.aboucorp.variantchess", ex.getMessage());
    }
}
