package fr.aboucorp.variantchess.app.views.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.heroiclabs.nakama.api.User;

public abstract class VariantChessActivity extends FragmentActivity {
    public abstract void setFragment(Class<? extends Fragment> fragmentClass, String tag);

    public abstract void userIsConnected(User nakamaUser);
}
