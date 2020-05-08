package fr.aboucorp.variantchess.app.views.activities;

import androidx.appcompat.app.AppCompatActivity;

public abstract class VariantChessActivity extends AppCompatActivity {
    protected abstract void bindViews();
    protected abstract void bindListeners();
}
