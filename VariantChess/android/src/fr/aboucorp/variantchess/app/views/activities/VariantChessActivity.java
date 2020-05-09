package fr.aboucorp.variantchess.app.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public abstract class VariantChessActivity extends AppCompatActivity {
    public abstract void setFragment(Fragment fragment);

    protected abstract void bindViews();
    protected abstract void bindListeners();
}
