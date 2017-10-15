package ch.defiant.purplesky.api.promotions.internal;

import javax.inject.Singleton;

import ch.defiant.purplesky.api.promotions.IPromotionAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 */
@Module
public class PromotionModule {

    @Provides
//    @Singleton
    public IPromotionAdapter providePromotionAdapter(){
        return new PromotionAdapter();
    }
}
