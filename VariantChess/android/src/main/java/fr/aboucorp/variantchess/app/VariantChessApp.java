package fr.aboucorp.variantchess.app;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public final class VariantChessApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
