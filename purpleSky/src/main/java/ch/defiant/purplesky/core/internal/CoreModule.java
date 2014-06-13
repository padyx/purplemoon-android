package ch.defiant.purplesky.core.internal;

import javax.inject.Singleton;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.fragments.ChatListFragment;
import dagger.Module;
import dagger.Provides;

/**
 * @author  Patrick Bänziger
 */
@Module (
        complete = false,
        library = true,
        injects = {ChatListFragment.class}
)
public class CoreModule {

    @Provides
    public IMessageService provideMessageService(IPurplemoonAPIAdapter apiAdapter){
        return new MessageService(apiAdapter);
    }

}
