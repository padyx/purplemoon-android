package ch.defiant.purplesky.fragments.photovote;

import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.PhotoVoteBean;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.PurplemoonAPIAdapter;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.core.UserService.UserPreviewPictureSize;
import ch.defiant.purplesky.enums.ProfileStatus;
import ch.defiant.purplesky.fragments.DisplayProfileFragment;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.LayoutUtility;

import com.actionbarsherlock.app.SherlockListFragment;
import com.commonsware.cwac.endless.EndlessAdapter;
import com.squareup.picasso.Picasso;

public class PhotoVoteListFragment extends SherlockListFragment {

    public static final String EXTRA_BOOL_SHOWGIVEN = "given";
    private boolean m_showGiven;
    private PhotoVoteEndlessAdapter m_endlessAdapter;
    private PhotoVoteAdapter m_innerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_showGiven = getArguments().getBoolean(EXTRA_BOOL_SHOWGIVEN, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        m_innerAdapter = new PhotoVoteAdapter(getSherlockActivity(), 0);
        m_endlessAdapter = new PhotoVoteEndlessAdapter(getSherlockActivity(), m_innerAdapter, R.layout.loading_listitem);
        setListAdapter(m_endlessAdapter);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(new ClickListener());
    }

    private class ClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PhotoVoteBean bean = (PhotoVoteBean) parent.getItemAtPosition(position);
            if (bean == null) {
                return;
            }
            if (bean.getUser() != null && ProfileStatus.OK == bean.getUser().getProfileStatus()) {
                DisplayProfileFragment f = new DisplayProfileFragment();
                Bundle args = new Bundle();
                args.putString(ArgumentConstants.ARG_USERID, bean.getUser().getUserId());
                f.setArguments(args);

                FragmentTransaction t = getSherlockActivity().getSupportFragmentManager().beginTransaction();
                t.replace(R.id.fragment_container_frame, f).addToBackStack(null).commit();
            } else if (bean.getUser() != null) {
                // Status is not ok
                String status = bean.getUser().getProfileStatus().getLocalizedString();
                Toast.makeText(getSherlockActivity(), status, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class PhotoVoteEndlessAdapter extends EndlessAdapter {

        public PhotoVoteEndlessAdapter(Context context, ListAdapter wrapped, int pendingResource) {
            super(context, wrapped, pendingResource);
        }

        private final AtomicInteger m_currentCount = new AtomicInteger(0);
        private List<PhotoVoteBean> m_data;

        @Override
        protected void appendCachedData() {
            if (m_data != null) {
                for (PhotoVoteBean bean : m_data) {
                    m_innerAdapter.add(bean);
                }
                m_currentCount.set(m_innerAdapter.getCount());
            }
        }

        @Override
        protected boolean cacheInBackground() throws Exception {
            AdapterOptions opts = new AdapterOptions();
            opts.setStart(m_currentCount.get());

            List<PhotoVoteBean> votes;
            if (m_showGiven) {
                votes = PurplemoonAPIAdapter.getInstance().getGivenVotes(opts);
            } else {
                votes = PurplemoonAPIAdapter.getInstance().getReceivedVotes(opts);
            }

            boolean hasMore = votes != null && !votes.isEmpty();
            m_data = votes;
            return hasMore;
        }

    }

    private class PhotoVoteAdapter extends ArrayAdapter<PhotoVoteBean> {

        private class ViewHolder {
            ImageView imgV;
            TextView usernameLbl;
            TextView voteLbl;
            TextView dateLbl;
        }

        public PhotoVoteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View v;
            if (convertView == null) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.photovotelist_item, null);
                holder = new ViewHolder();
                holder.imgV = (ImageView) view.findViewById(R.id.photovotelist_item_imgV);
                holder.usernameLbl = (TextView) view.findViewById(R.id.photovotelist_item_usernameLbl);
                holder.voteLbl = (TextView) view.findViewById(R.id.photovotelist_item_voteLbl);
                holder.dateLbl = (TextView) view.findViewById(R.id.photovotelist_item_dateLbl);
                v = view;
                v.setTag(holder);
            } else {
                v = convertView;
                holder = (ViewHolder) v.getTag();
            }

            int imgSize = LayoutUtility.dpToPx(getResources(), 50);
            PhotoVoteBean item = getItem(position);
            if (item != null && item.getUser() != null) {
                URL u = UserService.getUserPreviewPicturUrl(item.getUser(), UserPreviewPictureSize.getPictureForPx(imgSize));
                holder.usernameLbl.setText(item.getUser().getUsername());
                if (u != null) {
                    holder.imgV.setTag(u.toString());
                    Picasso.with(getActivity()).load(u.toString()).placeholder(R.drawable.social_person).
                            error(R.drawable.no_image).resize(imgSize, imgSize).centerCrop().into(holder.imgV);
                } else {
                    holder.imgV.setImageResource(R.drawable.social_person);
                }
            } else {
                holder.imgV.setImageResource(R.drawable.social_person);
                holder.usernameLbl.setText(R.string.Unknown);
            }
            holder.voteLbl.setText(item.getVerdict().getResourceId());
            holder.dateLbl.setText(DateUtility.getMediumDateString(item.getTimestamp()));
            return v;
        }
    }
}
