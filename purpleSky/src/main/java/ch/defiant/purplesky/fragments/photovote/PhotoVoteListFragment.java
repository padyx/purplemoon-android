package ch.defiant.purplesky.fragments.photovote;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.DisplayProfileActivity;
import ch.defiant.purplesky.api.photovotes.IPhotoVoteAdapter;
import ch.defiant.purplesky.beans.PhotoVoteBean;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.enums.PhotoVoteVerdict;
import ch.defiant.purplesky.enums.profile.ProfileStatus;
import ch.defiant.purplesky.listeners.EndlessRecyclerViewScrollListener;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.LayoutUtility;

public class PhotoVoteListFragment extends Fragment {

    private static final String MODEL_TAG_PREFIX = PhotoVoteListFragment.class.getSimpleName();
    private static final String MODEL_TAG_GIVEN = "given";
    private static final String MODEL_TAG_RECEIVED = "received";

    public static final String TAG = PhotoVoteListFragment.class.getSimpleName();
    public static final String EXTRA_BOOL_SHOWGIVEN = "given";

    @Inject
    protected IPhotoVoteAdapter photoVoteAdapter;

    private boolean m_showGiven;

    PhotoVoteAdapter m_recycleViewAdapter;
    private EndlessRecyclerViewScrollListener endlessScrollListener;
    private PhotoVoteModelFragment m_model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PurpleSkyApplication.get().inject(this);

        if (getArguments() != null) {
            m_showGiven = getArguments().getBoolean(EXTRA_BOOL_SHOWGIVEN, false);
        }

        String tag = getModelTag();
        m_model = (PhotoVoteModelFragment) getFragmentManager().findFragmentByTag(tag);
        if (m_model == null) {
            m_model = new PhotoVoteModelFragment();
        }
        m_model.setShowGiven(m_showGiven);
        m_model.attach(this);
        reloadData();
    }


    private String getModelTag() {
        return MODEL_TAG_PREFIX + (m_showGiven ? MODEL_TAG_GIVEN : MODEL_TAG_RECEIVED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_recycleViewAdapter = new PhotoVoteAdapter(getActivity(), m_model.m_innerAdapter);

        View view = inflater.inflate(R.layout.layout_recyclerlist, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(m_recycleViewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        endlessScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (m_model.isHasMore()) {
                    AdapterOptions options = new AdapterOptions();
                    options.setStart(page * 20);
                    m_model.loadMore(options);
                }
            }
        };
        recyclerView.addOnScrollListener(endlessScrollListener);
        return view;
    }


    public void reloadData() {
        m_model.clear();
        m_recycleViewAdapter.notifyDataSetChanged();
        endlessScrollListener.resetState();
        AdapterOptions options = new AdapterOptions();
        options.setStart(0);
        m_model.loadMore(options);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // getListView().setOnItemClickListener(new ClickListener());
    }

    private class PhotoVoteClickListener implements View.OnClickListener {

        public int position;

        @Override
        public void onClick(View v) {
            PhotoVoteBean bean = m_model.m_innerAdapter.getItem(position);
            if (bean == null) {
                return;
            }
            if (bean.getUser() != null && ProfileStatus.OK == bean.getUser().getProfileStatus()) {
                Intent intent = new Intent(getActivity(), DisplayProfileActivity.class);
                intent.putExtra(ArgumentConstants.ARG_USERID, bean.getUser().getUserId());
                startActivity(intent);
            } else if (bean.getUser() != null) {
                // Status is not ok
                String status = bean.getUser().getProfileStatus().getLocalizedString();
                Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class PhotoVoteAdapter extends RecyclerView.Adapter<PhotoVoteAdapter.ViewHolder> {

        private final LayoutInflater inflater;
        private final ArrayAdapter<PhotoVoteBean> m_adapter;

        PhotoVoteAdapter(Context context, ArrayAdapter<PhotoVoteBean> adapter) {
            inflater = LayoutInflater.from(context);
            m_adapter = adapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.photovotelist_item, parent, false);
            view.setOnClickListener(new PhotoVoteClickListener());
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final PhotoVoteBean item = m_adapter.getItem(position);

            if (item == null || item.getUser() == null) {
                holder.imgV.setImageResource(R.drawable.social_person);
                holder.usernameLbl.setText(R.string.Unknown);
            } else {

                int imgSize = LayoutUtility.dpToPx(getResources(), 50);
                if (item.getUser() != null) {
                    URL u = UserService.getUserPreviewPictureUrl(item.getUser(), UserService.UserPreviewPictureSize.getPictureForPx(imgSize));
                    holder.usernameLbl.setText(item.getUser().getUsername());
                    if (u != null) {
                        holder.imgV.setTag(u.toString());
                        Picasso.with(getActivity())
                                .load(u.toString())
                                .placeholder(R.drawable.social_person)
                                .error(R.drawable.no_image)
                                .resize(imgSize, imgSize)
                                .centerCrop()
                                .into(holder.imgV);
                    } else {
                        holder.imgV.setImageResource(R.drawable.social_person);
                    }
                }

                PhotoVoteVerdict verdict = item.getVerdict();
                holder.voteLbl.setText(verdict.getResourceId());
                holder.voteResultIcon.setImageResource(verdict.getIconId());
                holder.dateLbl.setText(DateUtility.getMediumDateString(item.getTimestamp()));
            }
        }

        @Override
        public int getItemCount() {
            return m_adapter.getCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgV;
            ImageView voteResultIcon;
            TextView usernameLbl;
            TextView voteLbl;
            TextView dateLbl;

            public ViewHolder(View itemView) {
                super(itemView);

                imgV = (ImageView) itemView.findViewById(R.id.photovotelist_item_imgV);
                usernameLbl = (TextView) itemView.findViewById(R.id.photovotelist_item_usernameLbl);
                voteLbl = (TextView) itemView.findViewById(R.id.photovotelist_item_voteLbl);
                dateLbl = (TextView) itemView.findViewById(R.id.photovotelist_item_dateLbl);
                voteResultIcon = (ImageView) itemView.findViewById(R.id.photovotelist_voteIcon);
            }
        }

    }
}
