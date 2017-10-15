package ch.defiant.purplesky.db.internal;

import ch.defiant.purplesky.db.IBundleDao;
import ch.defiant.purplesky.db.IDatabaseProvider;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 * @since 1.0.1
 */
@Module
public class DatabaseModule {

    @Provides
    public IDatabaseProvider provideDatabaseProvider(){
        return new DatabaseProvider();
    }

    @Provides
    public IBundleDao provideBundleDao(IDatabaseProvider dbProvider){
        return new BundleDao(dbProvider);
    }

}
