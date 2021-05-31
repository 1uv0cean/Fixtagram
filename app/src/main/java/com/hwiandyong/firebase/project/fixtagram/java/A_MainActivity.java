package com.hwiandyong.firebase.project.fixtagram.java;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.hwiandyong.firebase.project.fixtagram.R;
import com.hwiandyong.firebase.project.fixtagram.databinding.ActivityAMainBinding;
import com.hwiandyong.firebase.project.fixtagram.java.fragment.BeforeProgressFragment;
import com.hwiandyong.firebase.project.fixtagram.java.fragment.CompleteProgressFragment;
import com.hwiandyong.firebase.project.fixtagram.java.fragment.InProgressFragment;

public class A_MainActivity extends BaseActivity {
    private static final String TAG = "A_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAMainBinding binding = ActivityAMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Create the adapter that will return a fragment for each section
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private final Fragment[] mFragments = new Fragment[]{
                    new BeforeProgressFragment(),
                    new InProgressFragment(),
                    new CompleteProgressFragment()
            };
            private final String[] mFragmentNames = new String[]{
                    getString(R.string.heading_before_progress),
                    getString(R.string.heading_in_progress),
                    getString(R.string.heading_complete_progress)
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        binding.container.setAdapter(mPagerAdapter);
        binding.tabs.setupWithViewPager(binding.container);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        }else if(i == R.id.action_map) {
            startActivity(new Intent(this, A_MapActivity.class));
            finish();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

}



