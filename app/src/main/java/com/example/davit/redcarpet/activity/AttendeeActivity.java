package com.example.davit.redcarpet.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.davit.redcarpet.ApiConnector;
import com.example.davit.redcarpet.R;
import com.example.davit.redcarpet.Tools;
import com.example.davit.redcarpet.UserAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Formatter;


public class AttendeeActivity extends AppCompatActivity {


    private static final String INVITE_MESSAGE = "Hi, you are invited to the party %s starting at %s. Check RedCarpet for more info";
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
        list = (ListView) findViewById(R.id.allpartylist);

        View headerView = getLayoutInflater().inflate(R.layout.header_view, list, false);
        ((TextView) headerView.findViewById(R.id.headerTitle)).setText("Attendees");
        list.addHeaderView(headerView);
        TextView emptyText = (TextView) findViewById(R.id.notFoundText);
        emptyText.setText("No attendee yet");
        list.setEmptyView(emptyText);
        /*
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    JSONObject userClicked = attendees.getJSONObject(position-1);
                    Intent showDetails = new Intent(getApplicationContext(), ViewProfileActivity.class);
                    showDetails.putExtra("org_id", userClicked.getInt("id"));
                    startActivity(showDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });
        */
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
                if (jsonArray==null) {
                    if (!Tools.tokenIsValid()) {
                        Toast.makeText(getApplicationContext(), "You are disconnected", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error occurred, try again later", Toast.LENGTH_LONG).show();
                    }
                } else {
                    attendees = jsonArray;
                    fillAttendees();
                }
            }

        }.execute(new ApiConnector());
    }

    private void fillAttendees() {
        list.setAdapter(new UserAdapter(attendees,this, R.layout.attend_item));
    }

    public void back(View view) {
        finish();
    }

    public void inviteFriend(View view) {
        Boolean result=Tools.checkPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS}, Tools.PERMISSION_READ_CONTACTS);
        if (result != null && result )
            pickContact();

    }

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, Tools.PERMISSION_READ_CONTACTS);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode==Tools.PERMISSION_READ_CONTACTS) {
                if (Tools.isAllPermissionsGranted(grantResults)) {
                    pickContact();
                } else {
                    Toast.makeText(getBaseContext(),"Permission denied to access/send sms to contact",Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==Tools.PERMISSION_READ_CONTACTS) {
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
                            String message = new Formatter().format(INVITE_MESSAGE, partyName, partyDate).toString();
                            sms.sendTextMessage(number, null, String.valueOf(message), null, null);
                            Toast.makeText(getApplicationContext(), "Your friend " + name + " have been invited", Toast.LENGTH_SHORT).show();
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
}
