package fr.aboucorp.variantchess.app.di.modules;

import android.content.Context;
import android.content.SharedPreferences;

import dagger.Module;
import dagger.Provides;

@Module(includes = {ContextModule.class})
public class SharedPreferencesModule {
    @Provides
    SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences("nakama.session", Context.MODE_PRIVATE);
    }
}
