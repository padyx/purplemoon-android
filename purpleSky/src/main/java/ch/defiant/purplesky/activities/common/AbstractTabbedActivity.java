package ch.defiant.purplesky.activities.common;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Bänziger
 */
public abstract class AbstractTabbedActivity extends BaseFragmentActivity {

    StatePagerAdapter m_pagerAdapter;
    ViewPager m_viewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_viewpager);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        m_pagerAdapter =  new StatePagerAdapter(getFragmentManager());
        m_viewPager = (ViewPager) findViewById(R.id.viewpager);
        m_viewPager.setAdapter(m_pagerAdapter);

        PagerTabStrip strip = (PagerTabStrip) findViewById(R.id.viewpager_strip);
        strip.setDrawFullUnderline(false);
        strip.setTextColor(getResources().getColor(android.R.color.white));
        strip.setTabIndicatorColor(getResources().getColor(R.color.amber));


        // Specify that tabs should be displayed in the action bar.

        m_viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between pages, select the
                // corresponding tab.
                tabChanging(position);
            }
        });

    }

    protected abstract Fragment createItemAtPosition(int i);
    protected abstract CharSequence getTitleAtPosition(int i);
    public abstract int getFragmentCount();

    public class StatePagerAdapter extends FragmentStatePagerAdapter {

        private final Map<Integer, Fragment> m_fragmentCache = new HashMap<>();

        public StatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Do not call this method to obtain the fragment. Call {@link #getCachedItem}
         * @param pos position
         * @return
         */
        @Override
        @Deprecated
        public Fragment getItem(int pos) {
            Fragment f = createItemAtPosition(pos);
            m_fragmentCache.put(pos, f);
            return f;
        }

        public Fragment getCachedItem(int i){
            return m_fragmentCache.get(i);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            m_fragmentCache.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return getFragmentCount();
        }

        @Override
        public CharSequence getPageTitle(int i) {
            return getTitleAtPosition(i);
        }
    }

    public Fragment getCurrentTabFragment() {
        return m_pagerAdapter.getCachedItem(m_viewPager.getCurrentItem());
    }

    protected void tabChanging(int position){}

}
