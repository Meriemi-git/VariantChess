package fr.aboucorp.variantchess.app.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.aboucorp.variantchess.app.multiplayer.NakamaManager;

@Module
public class NakamaModule {
    @Provides
    @Singleton
    NakamaManager provideNakamaManager() {
        return new NakamaManager();
    }
}
