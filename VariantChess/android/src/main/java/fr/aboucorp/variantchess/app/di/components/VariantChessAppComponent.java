package fr.aboucorp.variantchess.app.di.components;


import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import fr.aboucorp.variantchess.app.VariantChessApplication;
import fr.aboucorp.variantchess.app.di.modules.MainActivityModule;
import fr.aboucorp.variantchess.app.di.modules.NakamaModule;
import fr.aboucorp.variantchess.app.di.modules.fragments.BoardFragmentModule;
import fr.aboucorp.variantchess.app.di.modules.fragments.GameRulesFragmentModule;
import fr.aboucorp.variantchess.app.di.modules.fragments.MatchmakingFragmentModule;
import fr.aboucorp.variantchess.app.di.modules.fragments.SignInFragmentModule;
import fr.aboucorp.variantchess.app.di.modules.fragments.SignUpFragmentModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        MainActivityModule.class,
        GameRulesFragmentModule.class,
        BoardFragmentModule.class,
        MatchmakingFragmentModule.class,
        SignInFragmentModule.class,
        SignUpFragmentModule.class,
        NakamaModule.class
})

public interface VariantChessAppComponent {
    void inject(VariantChessApplication application);
}
