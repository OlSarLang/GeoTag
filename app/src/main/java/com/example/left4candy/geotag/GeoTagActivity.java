package com.example.left4candy.geotag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GeoTagActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView textViewUserEmail;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_tag);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        FirebaseUser user = mAuth.getCurrentUser();

        textViewUserEmail = (TextView)  findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("Welcome " + user.getEmail());

        logoutButton = (Button) findViewById(R.id.logoutButton);
    }

    public void logoutButtonClicked(){
        mAuth.signOut();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
