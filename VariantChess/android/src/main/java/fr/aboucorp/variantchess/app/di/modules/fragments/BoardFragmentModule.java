package fr.aboucorp.variantchess.app.di.modules.fragments;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import fr.aboucorp.variantchess.app.views.fragments.BoardFragment;

@Module
public abstract class BoardFragmentModule {
    @ContributesAndroidInjector
    abstract BoardFragment contributeBoardFragment();
}
