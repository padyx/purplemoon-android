package ch.defiant.purplesky.dao.internal;

import ch.defiant.purplesky.dao.IPendingMessageDao;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 */
@Module(library = true)
public class DaoModule {

    @Provides
    public IPendingMessageDao providePendingMessageDao(){
        return new PendingMessageDao();
    }
}
