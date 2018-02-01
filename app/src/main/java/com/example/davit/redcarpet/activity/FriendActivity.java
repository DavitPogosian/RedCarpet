package com.example.davit.redcarpet.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

public class FriendActivity extends AppCompatActivity {

    private static final String TAG = "FriendActivity";


    ListView friendsList;

    SharedPreferences sp;
    private static final String sp_Name = "userinfo";
    private static final String phonNumber_sp = "Number";
    private static final String user_id_sp = "ID";
    private static final String user_name_sp = "Name";
    int id;
    String user_number;
    String user_name;
    private Map<String, String> contactList;
    private Dialog loading;

    JSONArray registredUsersLists=new JSONArray();
    private TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend);
        friendsList = findViewById(R.id.friendslist);
        View headerView = getLayoutInflater().inflate(R.layout.header_view, friendsList, false);
        header = headerView.findViewById(R.id.headerTitle);
        header.setText("Friends");
        friendsList.addHeaderView(headerView);
        friendsList.setEmptyView(findViewById(R.id.notFoundText));

        sp = getSharedPreferences(sp_Name, MODE_PRIVATE);
        id = sp.getInt(user_id_sp, 0);
        user_number = sp.getString(phonNumber_sp, "");
        user_name = sp.getString(user_name_sp, "");
        if ((id==0) || (user_number.length()==0) ||(user_name.length()==0))
        {
            finish();
        }
        contactList = getContactList();
        if (contactList!=null)
            new FriendActivity.CheckContact().execute(new ApiConnector());
        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    JSONObject userClicked = registredUsersLists.getJSONObject(position-1);
                    Intent showDetails = new Intent(getApplicationContext(), ViewProfileActivity.class);
                    showDetails.putExtra("org_id", userClicked.getInt("id"));
                    startActivity(showDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }}});
    }


    private void gotoHome() {
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Tools.PERMISSION_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contactList = getContactList();
                    new FriendActivity.CheckContact().execute(new ApiConnector());
                } else {
                    Toast.makeText(getBaseContext(), "Allow access to your contacts so we can for registred friends.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
        }
    }

    private Map<String, String> getContactList() {
        Map<String, String> contactList = new HashMap();
        Boolean checkPermission = Tools.checkPermission(this, Manifest.permission.READ_CONTACTS, Tools.PERMISSION_READ_CONTACTS);
        if (checkPermission==null)
            return null;
        else if (!checkPermission) {
            Toast.makeText(getBaseContext(), "Permission denied to access your contact list.", Toast.LENGTH_SHORT).show();
        } else {
            loading = Tools.showLoading(this,"Checking for friends");
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    .toString().replaceAll(" ", "");
                            if (phoneNo !=null && !"".equals(phoneNo.trim())) {
                                contactList.put(phoneNo, name);
                                //Log.i("tag", "Name: " + name);
                                //Log.i("tag", "Phone Number: " + phoneNo);
                            }
                        }
                        pCur.close();
                    }
                }
            }
            if (cur != null) {
                cur.close();
            }
        }
        Log.i("end", contactList.keySet().toString());
        return contactList;
    }

    public void setListAdapter(JSONArray jsonArray)
    {
        this.registredUsersLists=jsonArray;
        Log.d("JSON_object","jsonArray_setListAdapter= "+jsonArray);
        this.friendsList.setAdapter(new UserAdapter(jsonArray,this));
    }

    private class CheckContact extends AsyncTask<ApiConnector,Long,JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... apiConnectors) {
            StringBuffer friends = new StringBuffer();
            for (String friend:contactList.keySet()) {
                friends.append(friend);
                friends.append(",");
            }
            Log.e("GetFriends", friends.toString());
            return apiConnectors[0].GetRegistredFriends(friends.toString());
        }
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (jsonArray==null) {
                if (!Tools.tokenIsValid()) {
                    Toast.makeText(getApplicationContext(), "You are disconnected", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to check friends. please try again later.", Toast.LENGTH_SHORT).show();
                }

            }else if (jsonArray.length()==0) {
                Toast.makeText(getBaseContext(), "No friends found, please invite them.", Toast.LENGTH_SHORT).show();
            }
            setListAdapter(jsonArray);
            loading.cancel();
        }
    }
}
