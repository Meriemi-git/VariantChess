package fr.aboucorp.variantchess.app.di.modules.fragments;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import fr.aboucorp.variantchess.app.views.fragments.GameRulesFragment;

@Module
public abstract class GameRulesFragmentModule {
    @ContributesAndroidInjector()
    abstract GameRulesFragment contributeGameRulesFragment();
}
