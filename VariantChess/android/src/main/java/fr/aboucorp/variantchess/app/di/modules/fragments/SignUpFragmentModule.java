package fr.aboucorp.variantchess.app.di.modules.fragments;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import fr.aboucorp.variantchess.app.views.fragments.SignUpFragment;

@Module
public abstract class SignUpFragmentModule {
        @ContributesAndroidInjector
        abstract SignUpFragment contributeSignUpFragment();
}
