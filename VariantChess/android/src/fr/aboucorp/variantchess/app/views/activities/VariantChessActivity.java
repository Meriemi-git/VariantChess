package fr.aboucorp.variantchess.app.views.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.heroiclabs.nakama.api.User;

public abstract class VariantChessActivity extends AppCompatActivity {
    public abstract void setFragment(Class fragmentClass, String tag, Bundle args);

    public abstract void requestForMailLink(String mail, String googleToken);

    public abstract void requestForGoogleLink(String googleToken);

    public abstract void userIsConnected(User nakamaUser);

    public abstract void confirmLinkMailAccount(String mail, String password);

    public abstract void confirmLinkGoogleAccount(String mail, String password,String googleToken);
}
