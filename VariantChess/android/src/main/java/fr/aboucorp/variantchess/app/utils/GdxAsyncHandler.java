package fr.aboucorp.variantchess.app.utils;

import android.os.Handler;
import android.os.Looper;

import com.badlogic.gdx.Gdx;

public abstract class GdxAsyncHandler {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void startAndWait() {
        new Thread(() -> {
            Gdx.app.postRunnable(() -> {
                try {
                    this.execute();
                } catch (Exception ex) {
                    this.error(ex);
                } finally {
                    synchronized (this.lock2) {
                        this.lock2.notify();
                    }
                }
            });
            synchronized (this.lock2) {
                try {
                    this.lock2.wait();
                } catch (InterruptedException ex) {
                    this.error(ex);
                }
            }
            synchronized (this.lock1) {
                this.lock1.notify();
            }
        }).start();
        synchronized (this.lock1) {
            try {
                this.lock1.wait();
            } catch (InterruptedException ex) {
                this.error(ex);
            }
        }
    }

    public abstract Object execute() throws Exception;

    public void start() {
        new Thread(() -> Gdx.app.postRunnable(() -> {
            try {
                Object args = this.execute();
                new Handler(Looper.getMainLooper()).post(() -> this.callbackOnUI(args));
            } catch (Exception ex) {
                new Handler(Looper.getMainLooper()).post(() -> this.error(ex));
                ex.printStackTrace();
            }
        })).start();
    }

    public void callbackOnUI(Object arg) {
    }

    public void error(Exception ex) {
    }
}
