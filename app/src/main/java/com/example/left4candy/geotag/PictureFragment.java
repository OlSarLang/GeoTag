package com.example.left4candy.geotag;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PictureFragment extends Fragment {

    public PictureFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.picturefragment, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.pictureView);
        return rootView;
    }
}
