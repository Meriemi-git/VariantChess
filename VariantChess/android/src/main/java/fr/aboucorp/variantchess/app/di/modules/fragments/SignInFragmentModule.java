package fr.aboucorp.variantchess.app.di.modules.fragments;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import fr.aboucorp.variantchess.app.views.fragments.SignInFragment;

@Module
public abstract class SignInFragmentModule {
    @ContributesAndroidInjector
    abstract SignInFragment contributeSinginFragment();
}
