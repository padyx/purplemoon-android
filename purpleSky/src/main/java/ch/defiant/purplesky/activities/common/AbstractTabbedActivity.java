package ch.defiant.purplesky.activities.common;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Bänziger
 */
public abstract class AbstractTabbedActivity extends BaseFragmentActivity {

    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
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

        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
                final int position = tab.getPosition();
                tabChanging(position);
                m_viewPager.setCurrentItem(position);
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };
        m_viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between pages, select the
                // corresponding tab.
                getActionBar().setSelectedNavigationItem(position);
            }
        });


        // Add 3 tabs, specifying the tab's text and TabListener
        int fragmentCount = getFragmentCount();
        for (int i = 0; i < fragmentCount; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(getTitleAtPosition(i))
                            .setTabListener(tabListener));
        }
    }

    protected abstract Fragment createItemAtPosition(int i);
    protected abstract CharSequence getTitleAtPosition(int i);
    public abstract int getFragmentCount();

    public class StatePagerAdapter extends FragmentStatePagerAdapter {
        public StatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return createItemAtPosition(i);
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

    protected void tabChanging(int position){}

}
