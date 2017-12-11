package com.example.davit.redcarpet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class PartyDetailsActivity extends AppCompatActivity {

    RatingBar ratingBar;
    CircleImageView party_pic;
    int Party_id;

    private static final String TAG = "PartyDetailsActivity";

    SharedPreferences sp;
    private static final String sp_Name = "userinfo";
    private static final String phonNumber_sp = "Number";
    private static final String user_id_sp = "ID";
    private static final String user_name_sp = "Name";

    int id;
    String user_number;
    String user_name;

    TextView Name;
    TextView Date;
    TextView StartTime;
    TextView EndTime;
    TextView Adress;
    TextView AdressHInt;
    TextView Description;
    TextView OrgName;
    CircleImageView PartyImage;
    CircleImageView OrgImage;
    RatingBar Rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party_details);
        party_pic=(CircleImageView) findViewById(R.id.party_img);
        setButonText();
        setImages();
        Party_id = getIntent().getIntExtra("PartyID",-1);
        sp = getSharedPreferences(sp_Name, MODE_PRIVATE);
        id = sp.getInt(user_id_sp, 0);
        user_number = sp.getString(phonNumber_sp, "");
        user_name = sp.getString(user_name_sp, "");
        if ((id==0) || (user_number.length()==0) ||(user_name.length()==0))
        {
            gotoHome();
        }
        Log.e("Party_id",""+Party_id);

        Name = (TextView) findViewById(R.id.Name);
        OrgName = (TextView) findViewById(R.id.org_name);
        Date = (TextView) findViewById(R.id.date);
        StartTime = (TextView) findViewById(R.id.timestart);
        EndTime = (TextView) findViewById(R.id.timeend);
        Adress = (TextView) findViewById(R.id.adress_of);
        AdressHInt = (TextView) findViewById(R.id.adress_hint);
        Description = (TextView) findViewById(R.id.description);
        PartyImage = (CircleImageView) findViewById(R.id.party_img);
        OrgImage = (CircleImageView) findViewById(R.id.org_img);
        Rating = (RatingBar) findViewById(R.id.ratingBar);
        // TODO: 02/12/2017 get all info of party by id
        new PartyDetailsActivity.ShowPartyById().execute(new ApiConnector());
    }



    private class ShowPartyById extends AsyncTask<ApiConnector, Long, JSONArray>
    {
        private static final String  startImageUrl="https://redcarpetproject.000webhostapp.com/images/";

        @Override
        protected JSONArray doInBackground(ApiConnector... params)
        {
            return params[0].ShowPartyById(String.valueOf(Party_id));
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray)
        {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                /*
                [{"name":"second ","date":"7.11.2017","start":"11:11","end":"11:11","andress":"my addres ","adresshint":"","description":
                "","user_id":"4","image":"0488443770_second ","userimage":"def","username":"Davit","AVG(Reiting.rate)":null}]
                * */
                Name.setText(jsonObject.getString("name"));
                Date .setText(jsonObject.getString("date"));
                StartTime.setText(jsonObject.getString("start"));
                EndTime.setText(jsonObject.getString("end"));
                Adress.setText(jsonObject.getString("andress"));
                AdressHInt.setText(jsonObject.getString("adresshint"));
                Description.setText(jsonObject.getString("description"));
                String imageName=jsonObject.getString("image");
                String fullUrlForimg=startImageUrl+imageName+".jpg";
                Picasso.with(getApplicationContext()).load(fullUrlForimg)
                        .placeholder(R.drawable.party_def_ic)
                        .error(R.drawable.party_def_ic)
                        .into(PartyImage);
                Log.e(TAG,jsonObject.getString("userimage"));
                String userimageName=jsonObject.getString("userimage");
                String userimagefullUrlForimg=startImageUrl+userimageName+".jpg";
                Picasso.with(getApplicationContext()).load(userimagefullUrlForimg)
                        .placeholder(R.drawable.prof_pic_def)
                        .error(R.drawable.prof_pic_def)
                        .into(OrgImage);
                OrgName.setText(jsonObject.getString("username"));
                String r=jsonObject.getString("AVG(Reiting.rate)");
                if (r.equals("null"))
                    r="0";
                Rating.setRating(Float.valueOf(r));

                Log.e(TAG,userimagefullUrlForimg);
                // TODO: 11/12/2017 chi ashxatum useriimage , erb description@ kam addresshint@ null a tp cuyc chta TextViewner@ dizainel besamb vatna
                // TODO: 11/12/2017 stanal sax frendner@ ovqer nshel en vor kgnan patyin  texadrel hoizontal i mej menak nkarner@ u poqr anunner@ takic , amen mi itemin kcneluc redirectia kani et mardu profile

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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
    private void gotoHome() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
