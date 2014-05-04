package ch.defiant.purplesky.util;

import android.graphics.BitmapFactory;

public class ImageUtility {

    public static final String TAG = "ImageUtility";

    /**
     * Calculates the scaling factor for a bitmap. See also Android Training: "Loading large Bitmaps Efficiently"
     * 
     * @param options
     * @param targetHeight
     * @param targetWidth
     * @return
     * @author <a href='http://developer.android.com/training/displaying-bitmaps/index.html'>Android Training</a>
     */
    public static int calculateInsampleSize(BitmapFactory.Options options, int targetHeight, int targetWidth) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > targetHeight || width > targetWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) targetHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) targetWidth);
            }
        }
        return inSampleSize;
    }

}
