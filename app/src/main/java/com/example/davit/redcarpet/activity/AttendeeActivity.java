package com.example.davit.redcarpet.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.davit.redcarpet.ApiConnector;
import com.example.davit.redcarpet.R;
import com.example.davit.redcarpet.Tools;
import com.example.davit.redcarpet.UserAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Formatter;

import de.hdodenhof.circleimageview.CircleImageView;


public class AttendeeActivity extends AppCompatActivity {


    private static final String INVITE_MESSAGE = "Hi, you are invited to the party %s on %s starting from %s";
    private int partyId;
    private ListView list;
    private JSONArray attendees;
    private String partyDate;
    private String partyName;
    private String partyTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendees);
        //party_pic = (CircleImageView) findViewById(R.id.party_img);
        //setButonText();
        partyId = getIntent().getIntExtra("PartyID", -1);
        partyName = getIntent().getStringExtra("PartyName");
        partyDate = getIntent().getStringExtra("PartyDate");
        partyTime = getIntent().getStringExtra("PartyTime");
        list = (ListView) findViewById(R.id.allpartylist);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    JSONObject productClicked = attendees.getJSONObject(position);
                    Intent showDetails = new Intent(getApplicationContext(), PartyDetailsActivity.class);
                    showDetails.putExtra("PartyID", productClicked.getInt("Id"));
                    startActivity(showDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });
        getAllAttendees(partyId);
    }

    private void getAllAttendees(final int partyId) {
        new AsyncTask<ApiConnector, Long, JSONArray>() {
            @Override
            protected JSONArray doInBackground(ApiConnector... apiConnectors) {
                return apiConnectors[0].getAttendees(partyId);
            }
            @Override
            protected void onPostExecute(JSONArray jsonArray)
            {
                attendees = jsonArray;
                fillAttendees();
            }

        }.execute(new ApiConnector());
    }

    private void fillAttendees() {
        list.setAdapter(new UserAdapter(attendees,this));
    }

    public void back(View view) {
        finish();
    }

    public void inviteFriend(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, 1);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                            },
                            null, null, null);

                    //while (c!=null && c.moveToNext()) {
                    if (c != null && c.moveToFirst()) {
                        String number = c.getString(0);
                        int type = c.getInt(1);
                        String name = c.getString(2);
                        SmsManager sms = SmsManager.getDefault();
                        String message=new Formatter().format(INVITE_MESSAGE, partyName, partyDate,partyTime).toString();
                        sms.sendTextMessage(number, null, String.valueOf(message), null, null);
                        Toast.makeText(getApplicationContext(),"Your friend "+name+" have been invited",Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }
}
