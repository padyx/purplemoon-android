package ch.defiant.purplesky.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.defiant.purplesky.R;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;

public abstract class AbstractTabbedFragment extends SherlockFragment {

    private static final String TABSELECTION = AbstractTabbedFragment.class.getCanonicalName() + ".TABSELECTION";
    private AtomicReference<TabListener<?>> m_activeTabListener = new AtomicReference<AbstractTabbedFragment.TabListener<?>>();
    private TabPagerAdapter m_tabPagerAdapter;
    private ViewPager m_viewPager;
    private Map<String, Class<? extends Fragment>> m_fragmentClasses = new HashMap<String, Class<? extends Fragment>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        // setup action bar for tabs
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        getSherlockActivity().getSupportActionBar().removeAllTabs();
        View layout = inflater.inflate(R.layout.viewpager, container, false);
        m_viewPager = (ViewPager) layout.findViewById(R.id.viewpager);
        m_viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between pages, select the
                // corresponding tab.
                getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(position);
            }
        });
        m_viewPager.setAdapter(m_tabPagerAdapter);

        return layout;
    }

    @Override
    public void onDetach() {
        getSherlockActivity().getSupportActionBar().removeAllTabs();
        getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getSherlockActivity().setProgressBarIndeterminateVisibility(false);
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TABSELECTION, getSherlockActivity().getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(TABSELECTION)) {
            // Make sure to select the right tab
            int position = savedInstanceState.getInt(TABSELECTION);
            if(position >= 0){
                getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(position);
            }
        }
    }

    /**
     * Adds a tab to the activity.
     * 
     * @param textResource
     *            Resourced to be displayed as title
     * @param tag
     *            Fragment tag
     * @param fragmentclazz
     *            Class of the fragment to instantiate.
     */
    protected <T extends Fragment> void addTab(int textResource, String tag, Class<T> fragmentclazz) {
        m_fragmentClasses.put(tag, fragmentclazz);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        TabListener<T> listener = new TabListener<T>();

        m_tabPagerAdapter.addFragment(tag);
        Tab tab = actionBar.newTab().setText(textResource).setTabListener(listener);
        actionBar.addTab(tab);
    }

    /**
     * 
     * @param tag
     * @return
     */
    protected abstract Bundle createFragmentArgumentBundle(String tag);

    private Fragment createFragmentForTag(String tag) {
        Class<? extends Fragment> clazz = m_fragmentClasses.get(tag);
        String clazzName = clazz.getName();
        return Fragment.instantiate(getSherlockActivity(), clazzName, createFragmentArgumentBundle(tag));
    }

    protected TabPagerAdapter getPagerAdapter() {
        return m_tabPagerAdapter;
    }

    public TabListener<?> getActiveTabListener() {
        return m_activeTabListener.get();
    }

    private void setActiveTabListener(TabListener<?> activeTabListener) {
        m_activeTabListener.set(activeTabListener);
    }

    public class TabListener<T extends Fragment> implements ActionBar.TabListener {

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            int pos = tab.getPosition();
            m_viewPager.setCurrentItem(pos);

            setActiveTabListener(this);
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }

    }

    protected class TabPagerAdapter extends FragmentStatePagerAdapter {

        private List<String> m_fragmentTags = new ArrayList<String>();

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragments(List<String> list) {
            ViewPager pager = (ViewPager) getView().findViewById(R.id.viewpager);

            startUpdate(pager);
            for (String tag : list) {
                m_fragmentTags.add(tag);
            }
            finishUpdate(pager);
            notifyDataSetChanged();
        }

        public void addFragment(String tag) {
            addFragments(Collections.singletonList(tag));
        }

        @Override
        public Fragment getItem(int pos) {
            return createFragmentForTag(m_fragmentTags.get(pos));
        }

        @Override
        public int getCount() {
            return m_fragmentTags.size();
        }

    }
}
