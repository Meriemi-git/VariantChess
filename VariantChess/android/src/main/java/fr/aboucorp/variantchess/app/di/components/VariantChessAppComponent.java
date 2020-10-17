package fr.aboucorp.variantchess.app.di.components;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import fr.aboucorp.variantchess.app.VariantChessApplication;
import fr.aboucorp.variantchess.app.di.modules.NakamaManagerModule;
import fr.aboucorp.variantchess.app.di.modules.activities.MainActivityModule;
import fr.aboucorp.variantchess.app.di.modules.fragments.BoardFragmentModule;
import fr.aboucorp.variantchess.app.di.modules.fragments.GameRulesFragmentModule;
import fr.aboucorp.variantchess.app.di.modules.fragments.MatchmakingFragmentModule;
import fr.aboucorp.variantchess.app.di.modules.fragments.SignInFragmentModule;
import fr.aboucorp.variantchess.app.di.modules.fragments.SignUpFragmentModule;
import fr.aboucorp.variantchess.app.di.scopes.VariantChessScope;

@Component(modules = {
        AndroidInjectionModule.class,
        MainActivityModule.class,
        BoardFragmentModule.class,
        MatchmakingFragmentModule.class,
        SignInFragmentModule.class,
        SignUpFragmentModule.class,
        GameRulesFragmentModule.class,
        NakamaManagerModule.class}
)
@VariantChessScope
public interface VariantChessAppComponent {
    void inject(VariantChessApplication application);
}
