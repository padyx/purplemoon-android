package ch.defiant.purplesky.api.photovotes.internal;

import javax.inject.Singleton;

import ch.defiant.purplesky.api.photovotes.IPhotoVoteAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
@Module (library = true)
public class PhotoVoteModule {

    @Provides
    @Singleton
    public IPhotoVoteAdapter providePhotoVoteAdapter(){
        return new PhotoVoteAdapter();
    }

}
