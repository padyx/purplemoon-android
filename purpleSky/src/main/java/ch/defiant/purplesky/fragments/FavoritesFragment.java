package ch.defiant.purplesky.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.adapters.ErrorAdapter;
import ch.defiant.purplesky.beans.OnlineBean;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.constants.UIConstants;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.LayoutUtility;
import ch.defiant.purplesky.util.StringUtility;
import ch.defiant.purplesky.util.UserUtility;

public class FavoritesFragment extends BaseFragment implements LoaderCallbacks<Holder<List<OnlineBean>>> {

    public static final String TAG = FavoritesFragment.class.getSimpleName();

    private BaseAdapter m_adapter;
    private ListView m_listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
        m_adapter = new FavoritesAdapter(getSherlockActivity(), R.layout.usersearch_result_item);

        m_listView = (ListView) inflater.inflate(R.layout.list_plain, container, false);
        m_listView.setAdapter(m_adapter);

        m_listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnlineBean item = (OnlineBean) m_adapter.getItem(position);
                if (item != null && StringUtility.isNotNullOrEmpty(item.getProfileId())) {
                    DisplayProfileFragment f = new DisplayProfileFragment();
                    Bundle args = new Bundle();
                    args.putString(ArgumentConstants.ARG_USERID, item.getProfileId());
                    f.setArguments(args);

                    FragmentTransaction t = getSherlockActivity().getSupportFragmentManager().beginTransaction();
                    t.replace(R.id.fragment_container_frame, f).addToBackStack(null).commit();
                }
            }

        });

        getOrUpdateData();
        return m_listView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSherlockActivity().getSupportActionBar().setTitle(R.string.Favorites_Online_);
    }

    private void getOrUpdateData() {
        getLoaderManager().initLoader(R.id.loader_favorites_main, null, this);
    }

    private class FavoritesAdapter extends ArrayAdapter<OnlineBean> {

        public class ViewHolder {
            TextView usernameLbl;
            TextView descriptionLbl;
            TextView statusLbl;
            ImageView userImgV;
        }

        private static final int POSITION = R.id.usersearch_result_usernameLbl;

        public FavoritesAdapter(Context context, int resource) {
            super(context, resource, new ArrayList<OnlineBean>());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            ViewHolder holder = null;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.usersearch_result_item, null);
                holder = new ViewHolder();
                holder.usernameLbl = (TextView) v.findViewById(R.id.usersearch_result_usernameLbl);
                holder.descriptionLbl = (TextView) v.findViewById(R.id.usersearch_result_item_description);
                holder.statusLbl = (TextView) v.findViewById(R.id.usersearch_result_item_onlinestatus);
                holder.userImgV = (ImageView) v.findViewById(R.id.usersearch_result_userImgV);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            v.setTag(POSITION, Integer.valueOf(position));

            int imgSize = LayoutUtility.dpToPx(getResources(), 50);
            OnlineBean item = (OnlineBean) m_adapter.getItem(position);
            CharSequence description = null;
            if (item != null) {
                if (item.getUserBean() != null) {
                    // Username
                    holder.usernameLbl.setText(item.getUserBean().getUsername());
                    URL url = UserService.getUserPreviewPicturUrl(item.getUserBean(),
                            UserService.UserPreviewPictureSize.getPictureForPx(imgSize));
                    if (url != null) {
                        Picasso.with(getActivity()).load(url.toString()).placeholder(R.drawable.social_person).
                                error(R.drawable.no_image).resize(imgSize, imgSize).centerCrop().into(holder.userImgV);
                    } else {
                        setImagePlaceHolder(holder);
                    }
                    description = UserUtility.createDescription(getContext(), item.getUserBean());
                }
                holder.descriptionLbl.setText(description);

                // TODO Custom status?
                // Status
//                String status = null;
//                if (StringUtility.isNotNullOrEmpty(item.getOnlineStatusText())) {
//                    status = item.getOnlineStatusText();
//                }
//                holder.statusLbl.setText(status);

                // Status image
                if (item.getOnlineStatus() != null) {
                    holder.statusLbl.setTextColor(getContext().getResources().getColor(item.getOnlineStatus().getColor()));
                    holder.statusLbl.setText(item.getOnlineStatus().getLocalizedString(getSherlockActivity()));
                } 
            }

            return v;
        }

        private void setImagePlaceHolder(ViewHolder holder) {
            holder.userImgV.setImageResource(UIConstants.PLACEHOLDER_USERIMG_RESID);
        }
    }

    @Override
    public Loader<Holder<List<OnlineBean>>> onCreateLoader(int arg0, Bundle arg1) {
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);

        return new SimpleAsyncLoader<Holder<List<OnlineBean>>>(getSherlockActivity()) {

            @Override
            public Holder<List<OnlineBean>> loadInBackground() {
                Holder<List<OnlineBean>> favorites = null;
                try {
                    favorites = new Holder<List<OnlineBean>>(apiAdapter.getOnlineFavorites());
                } catch (IOException e) {
                    return new Holder<List<OnlineBean>>(e);
                } catch (PurpleSkyException e) {
                    return new Holder<List<OnlineBean>>(e);
                }

                return favorites;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Holder<List<OnlineBean>>> loader, Holder<List<OnlineBean>> result) {
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);

        if (result != null && result.getContainedObject() != null) {
            if (m_adapter instanceof ArrayAdapter) {
                @SuppressWarnings("unchecked")
                ArrayAdapter<OnlineBean> arrayadapter = (ArrayAdapter<OnlineBean>) m_adapter;
                arrayadapter.clear();
                for (OnlineBean h : result.getContainedObject()) {
                    arrayadapter.add(h);
                }
            }
            int size = result.getContainedObject().size();
            PurpleSkyApplication.get().setEventCount(NavigationDrawerEventType.FAVORITES, size);
        } else {
            if (result.getException() instanceof IOException) {
                m_adapter = new ErrorAdapter(getSherlockActivity());
                m_listView.setAdapter(m_adapter);
            } else {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "Retrieving favorites got exception", result.getException());
                }
                m_adapter = new ErrorAdapter(R.string.UnknownErrorOccured, getSherlockActivity());
                m_listView.setAdapter(m_adapter);
            }
        }

        m_adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Holder<List<OnlineBean>>> arg0) {
    }

}
