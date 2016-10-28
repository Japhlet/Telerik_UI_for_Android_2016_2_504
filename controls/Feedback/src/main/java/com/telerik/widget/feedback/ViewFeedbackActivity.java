package com.telerik.widget.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ViewFeedbackActivity extends ActionBarActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_view_feedback);
        this.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayUseLogoEnabled(true);
        this.getSupportActionBar().setLogo(new ColorDrawable());
        this.viewPager = (ViewPager) this.findViewById(R.id.viewPager);
        this.viewPager.setAdapter(new FeedbackListsAdapter(this.getSupportFragmentManager()));

        final ActionBar supportActionBar = this.getSupportActionBar();
        for (int i = 0; i < 3; i++) {
            ActionBar.Tab actionTab = supportActionBar.newTab();
            if (i == 0) {
                actionTab.setText(getString(R.string.view_feedback_tab_open));

            } else if (i == 1) {
                actionTab.setText(getString(R.string.view_feedback_tab_resolved));
            } else {
                actionTab.setText(getString(R.string.view_feedback_tab_all));
            }
            actionTab.setTabListener(new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                }
            });
            supportActionBar.addTab(actionTab);
        }

        this.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                supportActionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_feedback_items_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.action_refresh) {
            this.refreshCurrentList();
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshCurrentList() {
        FeedbackListsAdapter adapter = (FeedbackListsAdapter) this.viewPager.getAdapter();
        if (adapter.currentFragment != null) {
            adapter.currentFragment.requestItems();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RadFeedback.instance().onSendFeedbackFinished();
    }

    class FeedbackListsAdapter extends FragmentStatePagerAdapter {

        FragmentItemsList currentFragment;

        public FeedbackListsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            currentFragment = (FragmentItemsList) object;
        }

        @Override
        public Fragment getItem(int position) {
            FragmentItemsList result = null;
            result = new FragmentItemsList();
            if (position == 0) {
                result.itemsFilter = RadFeedback.OPEN_ITEMS;
            } else if (position == 1) {
                result.itemsFilter = RadFeedback.RESOLVED_ITEMS;
            } else {
                result.itemsFilter = RadFeedback.ALL_ITEMS;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
