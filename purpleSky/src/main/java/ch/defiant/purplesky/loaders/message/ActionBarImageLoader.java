package ch.defiant.purplesky.loaders.message;

import java.io.IOException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.PurplemoonAPIAdapter;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.core.UserService.UserPreviewPictureSize;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.LayoutUtility;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class ActionBarImageLoader extends SimpleAsyncLoader<Drawable> {

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

    private final String m_userId;

    public ActionBarImageLoader(Context context, Bundle b) {
        super(context);
        m_userId = b.getString(ArgumentConstants.ARG_USERID);
        if(m_userId == null){
            throw new IllegalArgumentException("No userId received");
        }
    }

    @Override
    public Drawable loadInBackground() {
        int imgSize = LayoutUtility.dpToPx(getContext().getResources(), 50);
        try{
            MinimalUser user = PurplemoonAPIAdapter.getInstance().getMinimalUserData(m_userId, false);
            URL url = UserService.getUserPreviewPicturUrl(user, UserPreviewPictureSize.getPictureForPx(imgSize));
            if(url != null){
                P_Target target = new P_Target();
                Picasso.with(PurpleSkyApplication.getContext()).load(url.toString()).
                    resize(imgSize, imgSize).centerCrop().into(target);
                while(!target.finished && !isAbandoned() && !isReset()){ // TODO
                    Thread.sleep(250);
                }
                return target.m_bitmap;
            } 
        }
        catch(IOException e){
        } catch (Exception e) {
            Log.w(TAG, "Loading image of user failed with exception",e);
        }
        return null;
    }

}
