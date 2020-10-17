package fr.aboucorp.variantchess.app.di.modules.activities;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import fr.aboucorp.variantchess.app.di.scopes.MainActivityScope;
import fr.aboucorp.variantchess.app.views.MainActivity;

@Module
public abstract class MainActivityModule {
    @MainActivityScope
    @ContributesAndroidInjector
    abstract MainActivity contributeYourAndroidInjector();


}
