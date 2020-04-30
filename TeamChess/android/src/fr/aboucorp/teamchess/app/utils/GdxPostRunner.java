package fr.aboucorp.teamchess.app.utils;

import android.util.Log;

import com.badlogic.gdx.Gdx;

public abstract class GdxPostRunner {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            execute();
                        } catch (Exception ex) {
                            Log.e("fr.Aboucorp.teamchess",ex.getMessage());
                        } finally {
                            synchronized (lock2) {
                                lock2.notify();
                            }
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

    public abstract void execute();
}
