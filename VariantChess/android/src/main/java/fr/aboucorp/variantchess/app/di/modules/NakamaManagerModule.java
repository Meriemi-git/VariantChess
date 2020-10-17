package fr.aboucorp.variantchess.app.di.modules;


import com.heroiclabs.nakama.DefaultClient;

import dagger.Module;
import dagger.Provides;
import fr.aboucorp.variantchess.app.di.scopes.VariantChessScope;
import fr.aboucorp.variantchess.app.multiplayer.NakamaManager;
import fr.aboucorp.variantchess.app.multiplayer.NakamaSocketListener;

@Module
public class NakamaManagerModule {

    @Provides
    @VariantChessScope
    NakamaSocketListener nakamaSocketListener() {
        return new NakamaSocketListener();
    }

    @Provides
    @VariantChessScope
    DefaultClient defaultClient() {
        return new DefaultClient("defaultkey", "192.168.1.37", 7349, false);
    }

    @Provides
    @VariantChessScope
    NakamaManager nakamaManager(NakamaSocketListener nakamaSocketListener, DefaultClient defaultClient) {
        return new NakamaManager(nakamaSocketListener, defaultClient);
    }

}
