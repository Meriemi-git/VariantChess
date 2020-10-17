package fr.aboucorp.variantchess.app.di.components;

import dagger.Component;
import fr.aboucorp.variantchess.app.di.modules.NakamaManagerModule;
import fr.aboucorp.variantchess.app.di.modules.OnlineMatchManagerModule;
import fr.aboucorp.variantchess.app.di.scopes.VariantChessScope;
import fr.aboucorp.variantchess.app.managers.OnlineMatchManager;

@Component(modules = {OnlineMatchManagerModule.class, NakamaManagerModule.class})
@VariantChessScope
public interface OnlineMatchManagerComponent {
    void inject(OnlineMatchManager onlineMatchManager);
}
