package ch.defiant.purplesky.api.internal;

import javax.inject.Singleton;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 */
@Module
public class APIModule {

    //@Singleton
    @Provides
    IPurplemoonAPIAdapter provideAPI(){
        return new PurplemoonAPIAdapter();
    }
}
