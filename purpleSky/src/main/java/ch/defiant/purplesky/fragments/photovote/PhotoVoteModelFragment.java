package ch.defiant.purplesky.fragments.photovote;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import ch.defiant.purplesky.api.photovotes.IPhotoVoteAdapter;
import ch.defiant.purplesky.beans.PhotoVoteBean;
import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.fragments.common.ModelListFragment;
import ch.defiant.purplesky.util.CollectionUtil;


public class PhotoVoteModelFragment extends ModelListFragment<PhotoVoteBean> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_innerAdapter = new ArrayAdapter<>(getActivity(), 0);
    }

    private static final String TAG = PhotoVoteModelFragment.class.getSimpleName();

    @Inject
    IPhotoVoteAdapter adapter;
    ArrayAdapter<PhotoVoteBean> m_innerAdapter;

    private boolean m_showGiven;
    private boolean m_hasMore = true;

    private WeakReference<PhotoVoteListFragment> m_fragment;

    public void clear() {
        m_innerAdapter.clear();
    }

    public boolean isHasMore() {
        return m_hasMore;
    }

    public void setHasMore(boolean hasMore) {
        m_hasMore = hasMore;
    }

    public void setShowGiven(boolean showGiven) {
        m_showGiven = showGiven;
    }

    public void attach(PhotoVoteListFragment f) {
        m_fragment = new WeakReference<>(f);
    }

    @Override
    public List<PhotoVoteBean> loadInBackground(AdapterOptions options) throws Exception {
        if (m_showGiven) {
            return adapter.getGivenVotes(options);
        } else {
            return adapter.getReceivedVotes(options);
        }
    }

    @Override
    public void onLoadMoreCompleted(List<PhotoVoteBean> newData) {
        m_innerAdapter.addAll(newData);

        PhotoVoteListFragment fragment = m_fragment.get();
        if (fragment == null) {
            return;
        }

        if (CollectionUtil.isEmpty(newData)) {
            setHasMore(false);
        }


        int count = m_innerAdapter.getCount();
        m_innerAdapter.addAll(newData);
        fragment.m_recycleViewAdapter.notifyItemRangeInserted(count, newData.size());
    }

    @Override
    public void onLoadMoreError(Exception e) {
        Log.i(TAG, "Error occurred during loading of photovotes ", e);
    }
}
