package ch.defiant.purplesky.dao.internal;

import ch.defiant.purplesky.dao.IMessageDao;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 */
@Module(library = true)
public class DaoModule {

    @Provides
    public IMessageDao providePendingMessageDao(){
        return new MessageDao();
    }
}
