package fr.aboucorp.variantchess.app.views.fragments;


import android.support.v4.app.Fragment;

public abstract class VariantChessFragment extends Fragment {
    protected abstract void bindViews();
    protected abstract void bindListeners();
}
