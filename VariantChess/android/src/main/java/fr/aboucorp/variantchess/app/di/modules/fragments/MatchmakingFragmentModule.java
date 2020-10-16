package fr.aboucorp.variantchess.app.di.modules.fragments;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import fr.aboucorp.variantchess.app.views.fragments.MatchmakingFragment;

@Module
public abstract class MatchmakingFragmentModule {
    @ContributesAndroidInjector
    abstract MatchmakingFragment contributeMatchmakingFragment();
}