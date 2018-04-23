package com.example.left4candy.geotag;

import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsPageAdapter extends FragmentPagerAdapter {
    //private final List<SupportMapFragment> mFragmentList = new ArrayList<>(); //TODO <Fragment>?
    //private final List<String> mFragmentTitleList = new ArrayList<>();

    private static int NUM_ITEMS = 2;
    private final List<GeoTagMapFragment> mapFragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();
    private final List<PictureFragment> pictureFragmentList = new ArrayList<>();

    public void addMapFragment(GeoTagMapFragment fragment, String title){
        mapFragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    public void addPictureFragment(PictureFragment fragment, String title){
        pictureFragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    public SectionsPageAdapter(android.support.v4.app.FragmentManager fm){
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position){
        return fragmentTitleList.get(position);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position){
        switch (position) {
            case 0:
                return GeoTagMapFragment.newInstance();
            case 1:
                return PictureFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
            return NUM_ITEMS;
    }
}
