package com.olahackerearth.pratik.olaplaystudios.ui.portfolio;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.olahackerearth.pratik.olaplaystudios.R;
import com.olahackerearth.pratik.olaplaystudios.ui.AllSongsFragment;
import com.olahackerearth.pratik.olaplaystudios.ui.HistoryFragment;
import com.olahackerearth.pratik.olaplaystudios.ui.HomeActivity;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;

public class AppPortfolioActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_portfolio);

        initializeScreen();

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        AllSongsFragment all, downloaded, liked;
        HistoryFragment history;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragmentAt(position);
        }

        private Fragment getFragmentAt(int position) {
            Fragment fragment = null;
            switch(position) {
//                case 0:
//                    fragment = all=all==null?AllSongsFragment.newInstance(Constant.CONSTANT_SONG_ALL_SONG, songListMain):all;
//                    break;
//                case 1:
//                    fragment = downloaded=downloaded==null?AllSongsFragment.newInstance(Constant.CONSTANT_SONG_DOWNLOADED_SONG, songListMain):downloaded;
//                    break;
//                case 2:
//                    fragment = liked=liked==null?AllSongsFragment.newInstance(Constant.CONSTANT_SONG_FAVORITE_SONG, songListMain):liked;
//                    break;
//                case 3:
//                    fragment = history=history==null?HistoryFragment.newInstance():history;
//                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All Songs";
                case 1:
                    return "Download";
                case 2:
                    return "Favorite";
                case 3:
                    return "History";
            }
            return null;
        }
    }

    /**
     * Basic Initializing function
     */
    public void initializeScreen() {

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.home_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.home_tabs);
        tabLayout.setupWithViewPager(mViewPager);
//        setupTabIcons();
    }

    private void setupTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(0).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(0).setIcon(tabIcons[2]);
    }
}
