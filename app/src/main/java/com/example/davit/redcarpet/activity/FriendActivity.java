package com.example.davit.redcarpet.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.davit.redcarpet.ApiConnector;
import com.example.davit.redcarpet.R;
import com.example.davit.redcarpet.UserAdapter;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class FriendActivity extends AppCompatActivity {



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend);
        friendsList = findViewById(R.id.friendslist);

        sp = getSharedPreferences(sp_Name, MODE_PRIVATE);
        id = sp.getInt(user_id_sp, 0);
        user_number = sp.getString(phonNumber_sp, "");
        user_name = sp.getString(user_name_sp, "");
        if ((id==0) || (user_number.length()==0) ||(user_name.length()==0))
        {
            finish();
        }
        contactList = getContactList();

        new FriendActivity.CheckContact().execute(new ApiConnector());

    }


    private void gotoHome() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    private Map<String, String> getContactList() {
        Map<String, String> contactList = new HashMap();
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
                                .toString().replaceAll(" ","");
                        contactList.put(phoneNo, name);
                        Log.i("tag", "Name: " + name);
                        Log.i("tag", "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        Log.i("end", contactList.keySet().toString());
        return contactList;
    }

    public void setListAdapter(JSONArray jsonArray)
    {
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
            return apiConnectors[0].GetRegistredFriends(friends.toString());
        }
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (jsonArray==null)
                Toast.makeText(getBaseContext(), "Unable to check friends. please try again later.", Toast.LENGTH_SHORT).show();
            setListAdapter(jsonArray);
        }
    }
}