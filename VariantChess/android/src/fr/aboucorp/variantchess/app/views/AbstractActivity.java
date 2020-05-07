package fr.aboucorp.variantchess.app.views;

import android.app.Activity;

public abstract class AbstractActivity extends Activity {
    protected abstract void bindViews();
    protected abstract void bindListeners();
}
