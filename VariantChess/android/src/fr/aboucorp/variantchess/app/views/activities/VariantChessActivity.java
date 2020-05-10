package fr.aboucorp.variantchess.app.views.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class VariantChessActivity extends AppCompatActivity {
    public abstract void setFragment(Class fragmentClass, String tag, Bundle args);

    protected abstract void bindViews();
    protected abstract void bindListeners();
}
