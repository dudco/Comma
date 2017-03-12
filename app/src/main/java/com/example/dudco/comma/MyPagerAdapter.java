package com.example.dudco.comma;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by dudco on 2017. 3. 12..
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> items;
    public MyPagerAdapter(FragmentManager fm, List items) {
        super(fm);
        this.items = items;
    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
