package com.example.davit.redcarpet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PartyDetailsActivity extends AppCompatActivity {

    RatingBar ratingBar;
    CircleImageView party_pic;
    int Party_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party_details);
        party_pic=(CircleImageView) findViewById(R.id.party_img);
        setButonText();
        setImages();
        Party_id = getIntent().getIntExtra("PartyID",-1);
        Log.e("Party_id",""+Party_id);
        // TODO: 02/12/2017 get all info of party by id
    }
    public void setButonText()
    {
        // will go if curent time <start time
        // chekin if current time > start time && current time < end time && status != checkin
        // chek out if current time > start time && current time < end time && status == checkin
        //gone if current time > end time
    }
    public void setImages()
    {
        //// TODO: 11/19/2017 use picaso to downloade images
    }
    public void getating(View v)
    {
        RatingBar r = (RatingBar) findViewById(R.id.ratingBar);
        Log.e("this",""+r.getRating());


    }

    public void zoom(View view)
    {
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.setImageDrawable(party_pic.getDrawable());
        imageView.setVisibility(View.VISIBLE);
    }
    public void zoomout(View view)
    {
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.setVisibility(View.GONE);
    }
}
