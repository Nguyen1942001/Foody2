package com.khoinguyen.foody2.Adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.khoinguyen.foody2.View.Fragments.AnGiFragment;
import com.khoinguyen.foody2.View.Fragments.OdauFragment;

public class AdapterViewPagerTrangChu extends FragmentStatePagerAdapter {

    AnGiFragment anGiFragment;
    OdauFragment odauFragment;

    public AdapterViewPagerTrangChu(@NonNull FragmentManager fm) {
        super(fm);
        anGiFragment = new AnGiFragment();
        odauFragment = new OdauFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return odauFragment;
            case 1:
                return anGiFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
