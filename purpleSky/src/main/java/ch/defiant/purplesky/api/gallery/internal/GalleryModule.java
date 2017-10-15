package ch.defiant.purplesky.api.gallery.internal;

import javax.inject.Singleton;

import ch.defiant.purplesky.api.gallery.IGalleryAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
@Module
public class GalleryModule {

    // @Singleton
    @Provides
    public IGalleryAdapter provideGalleryAdapter(){
        return new GalleryAdapter();
    }
}

