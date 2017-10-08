package ch.defiant.purplesky.loaders.promotions;

import android.content.Context;

import java.io.IOException;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.promotions.EventRegistrationResult;
import ch.defiant.purplesky.api.promotions.IPromotionAdapter;
import ch.defiant.purplesky.beans.promotion.Event;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;

/**
 * @author Patrick Bänziger
 */
public class RegisterUnregisterLoader extends SimpleAsyncLoader<Holder<EventRegistrationResult>> {

    private final boolean m_isUnregister;
    private final IPromotionAdapter m_adapter;
    private final int m_eventId;
    private final Event.RegistrationVisibility m_visibility;

    public RegisterUnregisterLoader(Context c, IPromotionAdapter adapter, boolean isUnregister,
                                    int eventId, Event.RegistrationVisibility visibility){
        super(c, R.id.loader_eventRegisterUnregister);
        m_isUnregister = isUnregister;
        m_adapter = adapter;
        m_eventId = eventId;
        m_visibility = visibility;
    }

    @Override
    public Holder<EventRegistrationResult> loadInBackground() {
        try {
            if (m_isUnregister) {
                return Holder.of(m_adapter.unregister(m_eventId));
            } else {
                return Holder.of(m_adapter.register(m_eventId, m_visibility));
            }
        } catch (IOException e) {
            return new Holder<>(e);
        } catch (PurpleSkyException e) {
            return new Holder<>(e);
        }
    }

}
