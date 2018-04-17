package com.example.left4candy.geotag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.google.firebase.storage.api.R;

public class PictureFragment extends Fragment {

    private Button mSelectImage;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorage;
    private static final int GALLERY_INTENT = 2;

    public PictureFragment(){
    }

    public static PictureFragment newInstance(){
        PictureFragment fragment = new PictureFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.picturefragment, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.pictureView);
        mStorage = FirebaseStorage.getInstance().getReference();
        mSelectImage = getActivity().findViewById(R.id.chooseImage);

        return rootView;
    }

    public void chooseImageClicked(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }
}
