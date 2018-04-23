package com.example.left4candy.geotag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PictureFragment extends Fragment {


    public PictureFragment(){
    }

    public static PictureFragment newInstance(){
        PictureFragment fragment = new PictureFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.picturefragment, container, false);

        return rootView;
    }
}
