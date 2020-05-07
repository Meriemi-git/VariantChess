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
                    execute();
                } catch (Exception ex) {
                    Log.e("fr.aboucorp.teamchess",ex.getMessage());
                } finally {
                    synchronized (lock2) {
                        lock2.notify();
                    }
                }
            });
            synchronized (lock2) {
                try {
                    lock2.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (lock1) {
                lock1.notify();
            }
        }).start();
        synchronized (lock1) {
            try {
                lock1.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startAsync(){
        new Thread(() -> Gdx.app.postRunnable(() -> {
            try {
                execute();
            } catch (Exception ex) {
                Log.e("fr.aboucorp.teamchess",ex.getMessage());
            }
        })).start();
    }

    public abstract void execute();
}
