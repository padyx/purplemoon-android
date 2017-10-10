package ch.defiant.purplesky.api.conversation.internal;

import javax.inject.Singleton;

import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
@Module
public class ConversationModule {

    // @Singleton
    @Provides
    public IConversationAdapter provideConversationAdapter(){
        return new ConversationAdapter();
    }
}
