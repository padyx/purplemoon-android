package ch.defiant.purplesky.loaders.promotions;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.promotions.IPromotionAdapter;
import ch.defiant.purplesky.beans.promotion.Promotion;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;

/**
 * @author Patrick Bänziger
 */
public class PromotionLoader extends SimpleAsyncLoader<Holder<List<Promotion>>> {

    private final IPromotionAdapter m_adapter;

    public PromotionLoader(Context c, @NonNull IPromotionAdapter adapter){
        super(c, R.id.loader_promotions);
        m_adapter = adapter;
    }

    @Override
    public Holder<List<Promotion>> loadInBackground() {
        try {
            return Holder.of(m_adapter.getActivePromotions());
        } catch (Exception e) {
            return new Holder<List<Promotion>>(e);
        }
    }
}
