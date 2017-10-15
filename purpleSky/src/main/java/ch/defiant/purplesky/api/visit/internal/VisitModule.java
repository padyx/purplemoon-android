package ch.defiant.purplesky.api.visit.internal;

import javax.inject.Singleton;

import ch.defiant.purplesky.api.visit.IVisitAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 */
@Module
public class VisitModule {

    @Provides
//    @Singleton
    public IVisitAdapter provideVisitAdapter(){
        return new VisitAdapter();
    }
}
