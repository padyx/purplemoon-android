package ch.defiant.purplesky.fragments.gallery;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.LightboxActivity;
import ch.defiant.purplesky.beans.Picture;
import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.enums.UserPictureSize;
import ch.defiant.purplesky.util.LayoutUtility;
import ch.defiant.purplesky.util.PictureUrlUtility;

public class PictureGridViewFragment extends Fragment {

    public static final String TAG = PictureGridViewFragment.class.getSimpleName();
    private PictureAdapter m_adapter;
    private PictureFolder m_folder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.grid_layout, container, false);

        GridView gv = (GridView) v.findViewById(R.id.grid_view);

        m_folder = (PictureFolder) getActivity().getIntent().getSerializableExtra(ArgumentConstants.ARG_FOLDER);
        m_adapter = new PictureAdapter(m_folder);
        gv.setAdapter(m_adapter);

        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), LightboxActivity.class);
                intent.putExtra(ArgumentConstants.ARG_FOLDER, m_folder);
                intent.putExtra(GallerySwipeFragment.ARG_START_POSITION, position);
                startActivity(intent);
            }
        });

        return v;
    }

    private class PictureAdapter extends BaseAdapter {

        private final PictureFolder m_data;
        private final int m_size;

        public PictureAdapter(PictureFolder folder) {
            m_data = folder;
            m_size = folder.getPictures().size();
        }

        @Override
        public int getCount() {
            return m_size;
        }

        @Override
        public Object getItem(int position) {
            return m_data.getPictures().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0; // All of same type
        }

        private class ViewHolder {
            ImageView imgV;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int px = LayoutUtility.dpToPx(getResources(), 96);

            View v = convertView;
            ViewHolder h;
            if (v == null) {
                v = LayoutInflater.from(getActivity()).inflate(R.layout.picturegrid_item, parent, false);
                h = new ViewHolder();
                h.imgV = (ImageView) v.findViewById(R.id.picturegrid_item_nwImgV);
                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
            }

            Picture picture = m_data.getPictures().get(position);
            UserPictureSize size = UserPictureSize.getPictureSizeForPx(px);
            String url = PictureUrlUtility.getPictureUrl(picture.getUrl(), size);

            Picasso.with(getActivity()).load(url).placeholder(R.drawable.picture_placeholder)
                    .error(R.drawable.no_image).resize(px, px).centerCrop().into(h.imgV);

            return v;
        }

    }

}
