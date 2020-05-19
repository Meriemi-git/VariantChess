package fr.aboucorp.variantchess.app.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.heroiclabs.nakama.api.User;

public abstract class VariantChessActivity extends FragmentActivity {
    public abstract void setFragment(Fragment fragment, String tag);

    public abstract void userIsConnected(User nakamaUser);
}
