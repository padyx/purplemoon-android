package ch.defiant.purplesky.fragments.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.constants.ArgumentConstants;

import com.actionbarsherlock.app.SherlockFragment;

public class GallerySwipeFragment extends SherlockFragment {

    private PictureFolder m_folder = null;
    private static final String TAG = GallerySwipeFragment.class.getSimpleName();
    public static final String ARG_START_POSITION = "position";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewpager, container, false);

        if (getArguments().getSerializable(ArgumentConstants.ARG_FOLDER) instanceof PictureFolder) {
            m_folder = (PictureFolder) getArguments().getSerializable(ArgumentConstants.ARG_FOLDER);
        } else {
            Log.e(TAG, "Tried to open conversation without user id");
            throw new IllegalArgumentException("Missing user id");
        }

        int startPos = getArguments().getInt(ARG_START_POSITION, 0);

        ViewPager pager = (ViewPager) v.findViewById(R.id.viewpager);
        pager.setAdapter(new ViewPagerAdapter(getSherlockActivity().getSupportFragmentManager()));
        if (startPos < m_folder.getPictureCount()) {
            pager.setCurrentItem(startPos);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hide action bar
        getSherlockActivity().getSupportActionBar().hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        getSherlockActivity().getSupportActionBar().show();
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if (m_folder == null || m_folder.getPictures() == null) {
                return 0;
            } else {
                return m_folder.getPictureCount();
            }
        }

        @Override
        public Fragment getItem(int position) {
            GalleryPictureFragment fragment = new GalleryPictureFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable(GalleryPictureFragment.ARGUMENT_PICTURE, m_folder.getPictures().get(position));
            fragment.setArguments(bundle);

            return fragment;
        }
    }

}
