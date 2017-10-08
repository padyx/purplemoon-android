package ch.defiant.purplesky.loaders.promotions;

import android.content.Context;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.promotions.IPromotionAdapter;
import ch.defiant.purplesky.beans.promotion.Event;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;

/**
 * @author Patrick Bänziger
 */
public class EventLoader extends SimpleAsyncLoader<Holder<Event>> {

    private final int eventId;
    private final IPromotionAdapter adapter;

    public EventLoader(int eventId, IPromotionAdapter adapter, Context c){
        super(c, R.id.loader_event);
        this.eventId = eventId;
        this.adapter = adapter;
    }

    @Override
    public Holder<Event> loadInBackground() {
        try {
            return Holder.of(adapter.getEvent(eventId));
        } catch (Exception e){
            return new Holder<>(e);
        }
    }
}
