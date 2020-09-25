package fr.aboucorp.variantchess.app.utils;

import android.util.Log;

import com.badlogic.gdx.Gdx;

public abstract class GdxPostRunner {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void start() {
        new Thread(() -> {
            Gdx.app.postRunnable(() -> {
                try {
                    this.execute();
                } catch (Exception ex) {
                    Log.e("fr.aboucorp.variantchess", ex.getMessage());
                } finally {
                    synchronized (this.lock2) {
                        this.lock2.notify();
                    }
                }
            });
            synchronized (this.lock2) {
                try {
                    this.lock2.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (this.lock1) {
                this.lock1.notify();
            }
        }).start();
        synchronized (this.lock1) {
            try {
                this.lock1.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void execute();

    public void startAsync() {
        new Thread(() -> Gdx.app.postRunnable(() -> {
            try {
                this.execute();
            } catch (Exception ex) {
                Log.e("fr.aboucorp.variantchess", ex.getMessage());
                ex.printStackTrace();
            }
        })).start();
    }
}
