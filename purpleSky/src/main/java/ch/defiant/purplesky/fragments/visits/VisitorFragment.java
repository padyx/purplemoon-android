package ch.defiant.purplesky.fragments.visits;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.DisplayProfileActivity;
import ch.defiant.purplesky.api.visit.IVisitAdapter;
import ch.defiant.purplesky.beans.AbstractVisitBean;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.VisitsMadeBean;
import ch.defiant.purplesky.beans.VisitsReceivedBean;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;
import ch.defiant.purplesky.fragments.BaseListFragment;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.LayoutUtility;
import ch.defiant.purplesky.util.UserUtility;

/**
 * List fragment showing visits. Can show either visitors or visits of the application user to other profiles.
 * 
 * @see #ARGUMENT_BOOLEAN_SHOWMYVISITS
 * 
 */
public class VisitorFragment extends BaseListFragment {

    @Inject
    protected IVisitAdapter visitAdapter;

    private static final String DATA = "data";
    private static final int MAXVISITS = 3;

    /**
     * Argument parameter for switching view type and content. <code>true</code> will switch to showing the application users visits to other
     * profiles. <code>false</code> (default) will show the other users' visits to the application users profile.
     */
    public static String ARGUMENT_BOOLEAN_SHOWMYVISITS = "showMyVisits";

    private VisitorListAdapter m_adapter;
    private boolean isShowOwnVisits;

    private EndlessVisitorAdapter m_endlessAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_adapter = new VisitorListAdapter(getActivity(), R.layout.uservisit_item);
        if (savedInstanceState != null) {
            // Try to restore the data
            @SuppressWarnings("unchecked")
            ArrayList<AbstractVisitBean> list = (ArrayList<AbstractVisitBean>) savedInstanceState.getSerializable(DATA);
            if (list != null) {
                for (AbstractVisitBean bean : list) {
                    m_adapter.add(bean);
                }
            }
        }
        // Make sure to wrap normal apiAdapter in the endlessadapter AFTER restoring state
        m_endlessAdapter = new EndlessVisitorAdapter(getActivity(), m_adapter, R.layout.loading_listitem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            isShowOwnVisits = arguments.getBoolean(ARGUMENT_BOOLEAN_SHOWMYVISITS, false);
        }

        View listview = super.onCreateView(inflater, container, savedInstanceState);
        setListAdapter(m_endlessAdapter);
        return listview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new OpenUserProfileListener());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<AbstractVisitBean> list = new ArrayList<AbstractVisitBean>();
        for (int i = 0, count = m_adapter.getCount(); i < count; i++) {
            list.add(m_adapter.getItem(i));
        }
        outState.putSerializable(DATA, list);
    }

    private class EndlessVisitorAdapter extends EndlessAdapter {

        public EndlessVisitorAdapter(Context context, VisitorListAdapter wrapped, int pendingResource) {
            super(context, wrapped, pendingResource);
        }

        private final AtomicInteger m_currentCount = new AtomicInteger(0);
        private List<AbstractVisitBean> m_data;
        private Date m_lastCheckDate;

        @Override
        protected boolean cacheInBackground() throws Exception {
            AdapterOptions options = new AdapterOptions();
            options.setStart(m_currentCount.get());

            if (m_currentCount.get() == 0) {
                m_lastCheckDate = new Date(); // TODO When refreshing, reset
            }

            boolean hasMore = false;
            final ArrayList<AbstractVisitBean> list = new ArrayList<AbstractVisitBean>();
            if (isShowOwnVisits) {
                List<VisitsMadeBean> own = visitAdapter.getOwnVists(options);
                if (own != null) {
                    list.addAll(own);
                }
            } else {
                List<VisitsReceivedBean> received = visitAdapter.getReceivedVists(options, m_lastCheckDate);
                if (received != null) {
                    list.addAll(received);
                }
                if(m_currentCount.get() == 0){
                    // If we looked at most recent, clear number from menu
                    PurpleSkyApplication.get().setEventCount(NavigationDrawerEventType.VISIT, 0);
                }
            }
            hasMore = !list.isEmpty();
            m_data = list;

            return hasMore;
        }

        @Override
        protected void appendCachedData() {
            if (m_data != null) {
                for (AbstractVisitBean bean : m_data) {
                    m_adapter.add(bean);
                }
                m_currentCount.set(m_adapter.getCount());

                // Reset menu counter
                PurpleSkyApplication.get().setEventCount(NavigationDrawerEventType.VISIT, 0);
            }
        }
    }

    private class VisitorListAdapter extends ArrayAdapter<AbstractVisitBean> {

        public VisitorListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        private class ViewHolder {
            ImageView userImgV;
            TextView userNameLbl;
            TextView descriptorLbl;
            TextView visitLbl;
        }

        private ViewHolder createViewHolder(View v) {
            ViewHolder h = new ViewHolder();
            h.userImgV = (ImageView) v.findViewById(R.id.uservisit_item_userImgV);
            h.userNameLbl = (TextView) v.findViewById(R.id.uservisit_item_userLbl);
            h.descriptorLbl = (TextView) v.findViewById(R.id.uservisit_item_descriptionLbl);
            h.visitLbl = (TextView) v.findViewById(R.id.uservisit_item_visitLbl);
            return h;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int px = LayoutUtility.dpToPx(getResources(), 50);

            View v = convertView;
            ViewHolder holder = null;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.uservisit_item, null);

                holder = createViewHolder(v);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            AbstractVisitBean item = m_adapter.getItem(position);

            int imgSize = LayoutUtility.dpToPx(getResources(), 50);
            holder.userImgV.setImageResource(R.drawable.picture_placeholder);
            if (item != null) {
                MinimalUser user = item.getUser();
                if (user != null) {
                    holder.userNameLbl.setText(user.getUsername());

                    URL url = UserService.getUserPreviewPictureUrl(user, UserService.UserPreviewPictureSize.getPictureForPx(px));
                    if (url != null) {
                        Picasso.with(getActivity()).load(url.toString()).error(R.drawable.no_image).
                                placeholder(R.drawable.social_person).resize(imgSize, imgSize).centerCrop().into(holder.userImgV);
                    } else {
                        holder.userImgV.setImageResource(R.drawable.social_person);
                    }

                    holder.descriptorLbl.setText(UserUtility.createDescription(getContext(), user));

                }
                TreeMap<Date, Boolean> visits = item.getVisits();
                StringBuilder sb = new StringBuilder();
                if (visits != null) {
                    Iterator<Entry<Date, Boolean>> iterator = visits.descendingMap().entrySet().iterator();
                    int visCnt = 0;
                    while (iterator.hasNext() && visCnt < MAXVISITS) {
                        visCnt++;
                        Entry<Date, Boolean> next = iterator.next();
                        sb.append(DateUtility.getMediumDateTimeString(next.getKey()));
                        if (item instanceof VisitsReceivedBean && ((VisitsReceivedBean) item).isUnseen()) {
                            sb.append("\t" + "<b>NEW</b>");
                        }
                        if (iterator.hasNext() && visCnt != MAXVISITS) {
                            sb.append("\n");
                        }
                    }
                }
                holder.visitLbl.setText(sb.toString());

            }

            return v;
        }
    }

    private class OpenUserProfileListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AbstractVisitBean bean = (AbstractVisitBean) parent.getItemAtPosition(position);
            if(bean != null) {
                String profileId = bean.getProfileId();
                if (profileId != null) {
                    Bundle args = new Bundle();
                    args.putString(ArgumentConstants.ARG_USERID, profileId);
                    Intent intent = new Intent(getActivity(), DisplayProfileActivity.class);
                    intent.putExtra(ArgumentConstants.ARG_USERID, profileId);
                    getActivity().startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.ErrorCouldNotFindUser), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
