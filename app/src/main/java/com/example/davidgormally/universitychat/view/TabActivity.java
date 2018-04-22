package com.example.davidgormally.universitychat.view;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.davidgormally.universitychat.Model.user.AppUser;
import com.example.davidgormally.universitychat.R;
import com.example.davidgormally.universitychat.controller.AppUserController;

public class TabActivity extends AppCompatActivity {

    private static final String TAG = "TabActivity";
    private static final String ARG = "userId";
    private AppUser appUser;

    public static Intent newIntent(Context context, String userId) {
        Intent intent = new Intent(context, TabActivity.class);
        intent.putExtra(ARG, userId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Log.d(TAG, "onCreate()");

        String userId = (String)getIntent().getSerializableExtra(ARG);
        AppUserController appUserController = new AppUserController(getApplicationContext());
        appUser = appUserController.getAppUser(userId);


        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.messages));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.students));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.account));

        final ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        final PagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }




    private class TabPagerAdapter extends FragmentPagerAdapter {

        int tabCount;

        public TabPagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            this.tabCount = tabCount;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    return MessageListFragment.newInstance(appUser.getAppUserId());

                case 1:
                    return new StudentListFragment();

                case 2:
                    return AccountFragment.newInstance(appUser.getAppUserId());

                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }
}


