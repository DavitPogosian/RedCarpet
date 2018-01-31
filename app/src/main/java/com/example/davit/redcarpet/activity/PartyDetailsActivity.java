package com.example.davit.redcarpet.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davit.redcarpet.ApiConnector;
import com.example.davit.redcarpet.R;
import com.example.davit.redcarpet.Tools;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PartyDetailsActivity extends AppCompatActivity {

    RatingBar ratingBar;
    CircleImageView party_pic;
    int Party_id;
    String PartyImageName;



    private static final String TAG = "PartyDetailsActivity";

    SharedPreferences sp;
    private static final String sp_Name = "userinfo";
    private static final String phonNumber_sp = "Number";
    private static final String user_id_sp = "ID";
    private static final String user_name_sp = "Name";

    int id;
    int orgId;
    String user_number;
    String user_name;

    TextView Name;
    TextView startDate;
    TextView endDate;
    TextView Adress;
    TextView AdressHInt;
    TextView Description;
    TextView OrgName;
    CircleImageView PartyImage;
    CircleImageView OrgImage;
    RatingBar Rating;
    int goingStatus = -1;
    Button goButton;
    private Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party_details);
        party_pic=(CircleImageView) findViewById(R.id.party_img);
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
        startDate = (TextView) findViewById(R.id.startDate);
        endDate = (TextView) findViewById(R.id.endDate);
        Adress = (TextView) findViewById(R.id.adress_of);
        AdressHInt = (TextView) findViewById(R.id.adress_hint);
        Description = (TextView) findViewById(R.id.description);
        PartyImage = (CircleImageView) findViewById(R.id.party_img);
        OrgImage = (CircleImageView) findViewById(R.id.org_img);
        Rating = (RatingBar) findViewById(R.id.ratingBar);
        goButton = (Button) findViewById(R.id.buttonGo);

        loading = Tools.showLoading(this);

        new PartyDetailsActivity.ShowPartyById().execute(new ApiConnector());
    }



    private class ShowPartyById extends AsyncTask<ApiConnector, Long, JSONArray>
    {
        private static final String  startImageUrl= Tools.IMAGES_URL;

        @Override
        protected JSONArray doInBackground(ApiConnector... params)
        {
            return params[0].ShowPartyById(String.valueOf(Party_id));
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray)
        {
            loading.dismiss();
            if (jsonArray==null) {
                if (!Tools.tokenIsValid()) {
                    Toast.makeText(getApplicationContext(), "You are disconnected", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred while getting party detail, try again later", Toast.LENGTH_LONG).show();
                }
            } else {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                /*
                [{"name":"second ","startDate":"2017-11-01 11:11","endDate":"2017-11-01 11:11","andress":"my addres ","adresshint":"","description":
                "","user_id":"4","image":"0488443770_second ","userimage":"def","username":"Davit","AVG(Reiting.rate)":null}]
                * */
                    Name.setText(jsonObject.getString("name"));
                    startDate.setText(Tools.formatDate(jsonObject.getString("startDate")));
                    endDate.setText(Tools.formatDate(jsonObject.getString("endDate")));
                    Adress.setText(jsonObject.getString("andress"));
                    if(jsonObject.getString("adresshint").length()==0)
                    {
                        AdressHInt.setVisibility(View.GONE);
                    }else{
                        AdressHInt.setText(jsonObject.getString("adresshint"));
                    }
                    if(jsonObject.getString("description").length()==0)
                    {
                        Description.setVisibility(View.GONE);
                    }else{
                        Description.setText(jsonObject.getString("description"));
                    }

                    Description.setText(jsonObject.getString("description"));
                    PartyImageName=jsonObject.getString("image");
                    String fullUrlForimg=startImageUrl+PartyImageName+".jpg";
                    Picasso.with(getApplicationContext()).load(fullUrlForimg)
                            .placeholder(R.drawable.party_def_ic)
                            .error(R.drawable.party_def_ic)
                            .into(PartyImage);
                    Log.e(TAG,jsonObject.getString("userimage"));
                    orgId  = jsonObject.getInt("user_id");
                    String userimageName=jsonObject.getString("userimage");
                    String userimagefullUrlForimg=startImageUrl+userimageName+".jpg";
                    Picasso.with(getApplicationContext()).load(userimagefullUrlForimg)
                            .placeholder(R.drawable.prof_pic_def)
                            .error(R.drawable.prof_pic_def)
                            .into(OrgImage);
                    OrgName.setText(jsonObject.getString("username"));
                    goingStatus=jsonObject.getInt("status");
                    String r=jsonObject.getString("AVG(Reiting.rate)");
                    if (r.equals("null"))
                        r="0";
                    Rating.setRating(Float.valueOf(r));

                    Log.e(TAG,userimagefullUrlForimg);

                    setButonText();
                    // TODO: 11/12/2017 (taza activityum sargel maket i tak dayle )  stanal sax frendner@ ovqer nshel en vor kgnan patyin  texadrel hoizontal i mej menak nkarner@ u poqr anunner@ takic , amen mi itemin kcneluc redirectia kani et mardu profile

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loading.cancel();

            }
        }
    }
    public void setButonText()
    {
        Calendar currentDate = Calendar.getInstance();
        Calendar eventDate= Tools.getDateFrom(startDate.getText().toString());
        Calendar endDateTime = Tools.getDateFrom(endDate.getText().toString());
        //startDate eventDate = new startDate();
        if (currentDate.before(eventDate)) {
            if (goingStatus==-1)
                goButton.setText(Tools.WILL_GO);
            else
                goButton.setText(Tools.GOING);
        } else {
            if (currentDate.before(endDateTime)) {
                if (goingStatus!=Tools.getStatusForLabel(Tools.CHECK_IN)) // checkIn
                    goButton.setText(Tools.CHECK_IN);
                else
                    goButton.setText(Tools.CHECK_OUT);
                // chekin if current time > start time && current time < end time && status != checkin
                // chek out if current time > start time && current time < end time && status == checkin
                //goButton.setText("Check in");
            } else {
                goButton.setText(Tools.GONE);
            }
        }
    }

    public void getrating(View v)
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

    public void goToOrgActivity(View view) {
        Intent intent = new Intent(this,ViewProfileActivity.class);
        intent.putExtra("org_id",orgId );
        startActivity(intent);

    }
    private void gotoHome() {
        finish();
    }
    public void goToChat(View view)
    {
        Intent go = new Intent(this, ChatActivity.class);
        go.putExtra("room_name",PartyImageName );
        startActivity(go);
    }
    public void showAttendees(View view) {
        Intent go = new Intent(this, AttendeeActivity.class);
        go.putExtra("PartyID",Party_id);
        go.putExtra("PartyName", Name.getText().toString());
        go.putExtra("PartyDate", startDate.getText().toString());
        startActivity(go);
    }


    public void registerAttend(View view) {
        goingStatus=Tools.getStatusForLabel(goButton.getText().toString());

        new AsyncTask<ApiConnector, Long, Boolean>() {
            @Override
            protected Boolean doInBackground(ApiConnector... apiConnectors) {
                return apiConnectors[0].attend(Party_id, goingStatus);
            }
            @Override
            protected void onPostExecute(Boolean registred)
            {
                if (registred==null) {
                    if (!Tools.tokenIsValid()) {
                        Toast.makeText(getApplicationContext(), "You are disconnected", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error occurred while getting party detail, try again later", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (registred) {
                        Toast.makeText(getBaseContext(), "Successfully registred to the party", Toast.LENGTH_SHORT).show();
                        setButonText();
                    } else
                        Toast.makeText(getBaseContext(), "Unable to register to party", Toast.LENGTH_SHORT).show();
                }
            }

        }.execute(new ApiConnector());
    }


}
