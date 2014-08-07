package ch.defiant.purplesky.core.internal;

import ch.defiant.purplesky.api.conversation.IConversationAdapter;
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
    public IMessageService provideMessageService(IConversationAdapter apiAdapter){
        return new MessageService(apiAdapter);
    }

}
