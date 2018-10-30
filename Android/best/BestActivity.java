package com.mobitant.bookapp.best;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.mobitant.bookapp.R;

public class BestActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Add Fragment
        adapter.AddFragment(new FragmentTotal(), "TOTAL");
        adapter.AddFragment(new FragmentNovel(), "NOVEL");
        adapter.AddFragment(new FragmentIt(), "IT");
        adapter.AddFragment(new FragmentSelf(), "SELF");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
