package com.example.plant_iot_phone;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mData;
    public MainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);

        mData = new ArrayList<>();
        mData.add(new TempGraph());
        mData.add(new HumiGraph());
        mData.add(new IlluGraph());

    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "온 도";
        else if(position == 1)
            return "습 도";
        else if(position == 2)
            return "조 도";
        return null;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) { return mData.get(position); }

    @Override
    public int getCount() { return mData.size(); }

    @Override
    public int getItemPosition(@NonNull Object object) { return POSITION_NONE; }
}
