package fr.aboucorp.variantchess.app.views.activities;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.heroiclabs.nakama.api.User;

public abstract class VariantChessActivity extends AppCompatActivity {
    public abstract void setFragment(Class<? extends Fragment> fragmentClass, String tag, Bundle args);

    public abstract void userIsConnected(User nakamaUser);
}
