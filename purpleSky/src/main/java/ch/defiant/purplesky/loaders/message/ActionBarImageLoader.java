package ch.defiant.purplesky.loaders.message;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.URL;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.core.UserService.UserPreviewPictureSize;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.LayoutUtility;

public class ActionBarImageLoader extends SimpleAsyncLoader<Drawable> {

    private final IPurplemoonAPIAdapter apiAdapter;
    private final String userId;

    private static final String TAG = ActionBarImageLoader.class.getSimpleName();

    private class P_Target implements Target {

        boolean finished = false;
        BitmapDrawable m_bitmap;

        @Override
        public void onPrepareLoad(Drawable arg0) {
        }

        @Override
        public void onBitmapLoaded(Bitmap arg0, LoadedFrom arg1) {
            finished = true;
            m_bitmap = new BitmapDrawable(getContext().getResources(), arg0);
        }

        @Override
        public void onBitmapFailed(Drawable arg0) {
            finished = true;
        }
    }

    public ActionBarImageLoader(Context context, Bundle b, IPurplemoonAPIAdapter apiAdapter) {
        super(context);
        this.apiAdapter = apiAdapter;
        userId = b.getString(ArgumentConstants.ARG_USERID);
        if(userId == null){
            throw new IllegalArgumentException("No userId received");
        }

    }

    @Override
    public Drawable loadInBackground() {
        int imgSize = LayoutUtility.dpToPx(getContext().getResources(), 50);
        try{
            MinimalUser user = apiAdapter.getMinimalUserData(userId, false);
            URL url = UserService.getUserPreviewPictureUrl(user, UserPreviewPictureSize.getPictureForPx(imgSize));
            if(url != null){
                Bitmap bitmap = Picasso.with(PurpleSkyApplication.get()).load(url.toString()).
                        resize(imgSize, imgSize).centerCrop().get();

                if(bitmap != null){
                    return new BitmapDrawable(getContext().getResources(),bitmap);
                }
            }
        }
        catch(IOException e){
        } catch (Exception e) {
            Log.w(TAG, "Loading image of user failed with exception",e);
        }
        return null;
    }

}
