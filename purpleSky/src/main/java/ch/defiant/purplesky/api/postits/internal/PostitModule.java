package ch.defiant.purplesky.api.postits.internal;

import javax.inject.Singleton;

import ch.defiant.purplesky.api.postits.IPostitAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
@Module (library = true)
public class PostitModule {

    @Provides
    @Singleton
    public IPostitAdapter providePostitAdapter(){
        return new PostitAdapter();
    }

}
