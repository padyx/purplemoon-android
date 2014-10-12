package ch.defiant.purplesky.fragments.gallery;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.Picture;
import ch.defiant.purplesky.enums.UserPictureSize;
import ch.defiant.purplesky.util.LayoutUtility;

public class GalleryPictureFragment extends Fragment {

    public static final String ARGUMENT_PICTURE = "picture";
    public static final String TAG = GalleryPictureFragment.class.getSimpleName();

    private Picture m_picture;
    private int maxSideLengthPx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.picturefragment, container, false);

        // We are cheating here (a bit). But pre-draw listeners are ugly, ugly, ugly. This should not be far off
        maxSideLengthPx = LayoutUtility.getMaximumDisplaySidePixels(getResources());

        if (getArguments() != null && getArguments().getSerializable(ARGUMENT_PICTURE) instanceof Picture) {
            m_picture = (Picture) getArguments().getSerializable(ARGUMENT_PICTURE);
        } else if (m_picture != null) {

        } else {
            // Oops
            Log.e(TAG, "No picture argument received!");
        }

        createGUI(inflated, m_picture);

        return inflated;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadImage();
    }

    /**
     * This method would fill additional image fields...
     * 
     * @param inflated
     * @param picture
     */
    private void createGUI(View inflated, Picture picture) {
    }

    public void loadImage() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Must measure view first
        ImageView imgView = (ImageView) getView().findViewById(R.id.picturefragment_image);

        // Get appropriate size
        UserPictureSize pictureSize = UserPictureSize.getPictureSizeForPx(maxSideLengthPx);

        Picasso.with(getActivity()).load(m_picture.getUrl() + pictureSize.getAPIValue()).
                placeholder(R.drawable.picture_placeholder).error(R.drawable.no_image).into(imgView);
    }
}
