package fr.aboucorp.variantchess.app.di.modules;

import com.heroiclabs.nakama.DefaultClient;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import fr.aboucorp.variantchess.app.multiplayer.NakamaManager;
import fr.aboucorp.variantchess.app.multiplayer.NakamaSocketListener;


@Module
@InstallIn(SingletonComponent.class)
public final class NakamaManagerModule {
    @Provides
    DefaultClient provideDefaultClient() {
        return new DefaultClient("defaultkey", "192.168.1.37", 7349, false);
    }

    @Provides
    NakamaSocketListener provideNakamaSocketListener(Lazy<NakamaManager> nakamaManager) {
        return new NakamaSocketListener(nakamaManager);
    }

}
