package ch.defiant.purplesky.core.internal;

import android.support.annotation.NonNull;

import java.util.Date;

import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.fragments.ChatListFragment;
import ch.defiant.purplesky.interfaces.IDateProvider;
import dagger.Module;
import dagger.Provides;

/**
 * @author  Patrick Bänziger
 */
@Module (

)
public class CoreModule {

    @Provides
    public IMessageService provideMessageService(IConversationAdapter apiAdapter){
        return new MessageService(apiAdapter);
    }

    @Provides
    public IDateProvider provideDateProvider(){
        return new IDateProvider() {
            @NonNull
            @Override
            public Date getDate() {
                return new Date();
            }
        };
    }

}
