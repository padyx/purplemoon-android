package ch.defiant.purplesky.fragments.postit;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.postits.IPostitAdapter;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PostIt;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;
import ch.defiant.purplesky.fragments.BaseListFragment;
import ch.defiant.purplesky.fragments.profile.DisplayProfileFragment;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.LayoutUtility;
import ch.defiant.purplesky.util.StringUtility;

public class PostitFragment extends BaseListFragment {

    public static final String ARGUMENT_BOOLEAN_SHOW_GIVEN = "given";
    private static final String DATA = "data";

    private PostitAdapter m_adapter;
    private boolean isShowGiven;

    private PostitEndlessAdapter m_endlessAdapter;


    @Inject
    protected IPostitAdapter postitAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_adapter = new PostitAdapter(getActivity(), R.layout.displaypostit_item);
        if (savedInstanceState != null) {
            // Try to restore the data
            @SuppressWarnings("unchecked")
            ArrayList<PostIt> list = (ArrayList<PostIt>) savedInstanceState.getSerializable(DATA);
            if (list != null) {
                for (PostIt bean : list) {
                    m_adapter.add(bean);
                }
            }
        }
        // Make sure to wrap normal apiAdapter in the endlessadapter AFTER restoring state
        m_endlessAdapter = new PostitEndlessAdapter(getActivity(), m_adapter, R.layout.loading_listitem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            isShowGiven = arguments.getBoolean(ARGUMENT_BOOLEAN_SHOW_GIVEN, false);
        }

        View v = super.onCreateView(inflater, container, savedInstanceState);
        setListAdapter(m_endlessAdapter);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(new OpenUserProfileListener());
        getListView().setDivider(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<PostIt> list = new ArrayList<PostIt>();
        for (int i = 0, count = m_adapter.getCount(); i < count; i++) {
            list.add(m_adapter.getItem(i));
        }
        outState.putSerializable(DATA, list);
    }

    private class PostitEndlessAdapter extends EndlessAdapter {

        private final AtomicInteger m_currentCount = new AtomicInteger(0);
        private List<PostIt> m_data;

        public PostitEndlessAdapter(Context context, ListAdapter wrapped, int pendingResource) {
            super(context, wrapped, pendingResource);
        }

        @Override
        protected boolean cacheInBackground() throws Exception {
            AdapterOptions options = new AdapterOptions();
            options.setStart(m_currentCount.get());

            List<PostIt> postIts;
            if (isShowGiven) {
                postIts = postitAdapter.getGivenPostIts(options);
            } else {
                postIts = postitAdapter.getReceivedPostIts(options);
                // If this does not throw an exception, mark as read
                PurpleSkyApplication.get().setEventCount(NavigationDrawerEventType.POSTIT, 0);
            }

            m_data = postIts;
            if (postIts != null && postIts.isEmpty()) {
                return false; // No more data
            } else
                return true;
        }

        @Override
        protected void appendCachedData() {
            if (m_data != null) {
                PostitAdapter adapter = (PostitAdapter) getWrappedAdapter();
                for (PostIt postit : m_data) {
                    adapter.add(postit);
                }
                m_currentCount.set(m_adapter.getCount());

                // Reset menu counter
                PurpleSkyApplication.get().setEventCount(NavigationDrawerEventType.POSTIT, 0);
            }
        }

    }

    private class PostitAdapter extends ArrayAdapter<PostIt> {

        public PostitAdapter(Context context, int resource) {
            super(context, resource, new ArrayList<PostIt>());
        }

        public class ViewHolder {
            TextView usernameLbl;
            TextView dateTimeLbl;
            TextView textLbl;
            TextView newLbl;
            TextView toFromLbl;
            ImageView userImgV;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int px = LayoutUtility.dpToPx(getResources(), 50);
            View v = convertView;
            ViewHolder holder = null;
            if (v == null) {
                LayoutInflater vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.displaypostit_item, null);

                holder = createViewHolder(v);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            PostIt item = m_adapter.getItem(position);

            boolean isNew = CompareUtility.equals(true, item.isNew());
            holder.newLbl.setVisibility(isNew ? View.VISIBLE : View.INVISIBLE);
            holder.toFromLbl.setText(isShowGiven ? R.string.to_person : R.string.by);
            holder.dateTimeLbl.setText(DateUtility.getMediumDateTimeString(item.getDate()));
            holder.textLbl.setText(item.getText());

            MinimalUser sender = item.getSender();
            if (sender != null) {
                holder.usernameLbl.setText(sender.getUsername());
                URL url = UserService.getUserPreviewPictureUrl(sender,
                        UserService.UserPreviewPictureSize.getPictureForPx(px));
                if (url != null) {
                    Picasso.with(getActivity()).load(url.toString()).placeholder(R.drawable.social_person).
                            error(R.drawable.no_image).resize(px, px).centerCrop().into(holder.userImgV);
                }
            }

            return v;
        }

        private ViewHolder createViewHolder(View v) {
            ViewHolder holder = new ViewHolder();
            holder.dateTimeLbl = (TextView) v.findViewById(R.id.displaypostit_item_dateLbl);
            holder.usernameLbl = (TextView) v.findViewById(R.id.displaypostit_item_usernameLbl);
            holder.textLbl = (TextView) v.findViewById(R.id.displaypostit_item_titleLbl);
            holder.newLbl = (TextView) v.findViewById(R.id.displaypostit_item_newLbl);
            holder.userImgV = (ImageView) v.findViewById(R.id.displaypostit_item_userImageView);
            holder.toFromLbl = (TextView) v.findViewById(R.id.displaypostit_item_toFromLbl);
            return holder;
        }

    }

    private class OpenUserProfileListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PostIt pos = (PostIt) parent.getItemAtPosition(position);
            MinimalUser user = pos.getSender();
            if (user != null && StringUtility.isNotNullOrEmpty(user.getUserId())) {
                DisplayProfileFragment f = new DisplayProfileFragment();
                Bundle args = new Bundle();
                args.putString(ArgumentConstants.ARG_USERID, user.getUserId());
                f.setArguments(args);

                FragmentTransaction t = getActivity().getFragmentManager().beginTransaction();
                t.replace(R.id.fragment_container_frame, f).addToBackStack(null).commit();
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.ErrorCouldNotFindUser), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
