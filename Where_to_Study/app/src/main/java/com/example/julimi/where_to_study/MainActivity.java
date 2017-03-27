package com.example.julimi.where_to_study;

/**
 * Created by Julimi on 2017-03-27.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.julimi.where_to_study.R;

import java.util.List;

/**
 * Created by Julimi on 2017-03-27.
 */

public class MainActivity extends AppCompatActivity
        implements BaseFragment.BaseExampleFragmentCallbacks {

    private final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);



        showFragment(new SearchViewFragment());
    }

    @Override
    public void onAttachSearchViewToDrawer(FloatingSearchView searchView) {
        searchView.attachNavigationDrawerToMenuButton(mDrawerLayout);
    }

    @Override
    public void onBackPressed() {
        List fragments = getSupportFragmentManager().getFragments();
        BaseFragment currentFragment = (BaseFragment) fragments.get(fragments.size() - 1);

        if (!currentFragment.onActivityBackPress()) {
            super.onBackPressed();
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }
}
