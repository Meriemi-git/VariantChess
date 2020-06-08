package fr.aboucorp.variantchess.app.views.fragments;


import androidx.fragment.app.Fragment;

public abstract class VariantChessFragment extends Fragment {
    public static final String ONLINE_ARGS_KEY = "isOnline";

    protected abstract void bindViews();

    protected abstract void bindListeners();
}
