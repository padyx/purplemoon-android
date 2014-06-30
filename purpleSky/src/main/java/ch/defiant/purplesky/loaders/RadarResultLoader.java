package ch.defiant.purplesky.loaders;

import android.content.Context;

import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.enums.UserSearchOrder;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.NVLUtility;

/**
 * Loader for the radar.
 * @author Patrick Bänziger
 * @since 1.1.0
*/
public class RadarResultLoader extends SimpleAsyncLoader<Holder<List<MinimalUser>>> {

    private static final int RESULT_NUMBER = 100;
    private final UserSearchOptions options;
    private final IPurplemoonAPIAdapter apiAdapter;

    public RadarResultLoader(Context context, IPurplemoonAPIAdapter adapter, UserSearchOptions options) {
        super(context, R.id.loader_radar_main);
        this.options = NVLUtility.nvl(options, new UserSearchOptions());
        this.apiAdapter = adapter;
    }

    @Override
    public Holder<List<MinimalUser>> loadInBackground() {
        options.setUserClass(MinimalUser.class);
        options.setNumber(RESULT_NUMBER);
        // If there is no filter set, require them to be online within last month...
        // TODO Set to last hour and retrieve more, if necessary
        options.setLastOnline(NVLUtility.nvl(options.getLastOnline(), UserSearchOptions.LastOnline.RECENTLY));
        options.setSearchOrder(UserSearchOrder.DISTANCE);

        try {
            List<MinimalUser> result = apiAdapter.searchUser(options);
            if(result.size() < RESULT_NUMBER/2){
                options.setLastOnline(UserSearchOptions.LastOnline.PAST_DAY);
                result = apiAdapter.searchUser(options);
            }
            return new Holder<List<MinimalUser>>(result);
        } catch (Exception e) {
            return new Holder<List<MinimalUser>>(e);
        }
    }
}
