package com.example.davit.redcarpet;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class FriendActivity extends AppCompatActivity {


    String Fiends="";
   //end: +4915207369761,+37455733590,+352621727758,0749999308,0037455597276,0037455597276,0732733780,0732733780,0744402155,
    // 0744402155,0721691314,0721691314,+41764526395,+41764526395,0720034933,0720034933,0213053857,+40729783738,+40729783738,
    // +352691531904,+352691531904,+33695759559,+33695759559,+352621512656,+352621512656,+380975206124,+380975206124,+32483146637,
    // +32483146637,+352661140544,+352661140544,+352621328364,+352621328364,+352661900823,+352661900823,+33760057937,+33760057937,
    // +37493186068,+37493186068,


    SharedPreferences sp;
    private static final String sp_Name = "userinfo";
    private static final String phonNumber_sp = "Number";
    private static final String user_id_sp = "ID";
    private static final String user_name_sp = "Name";
    int id;
    String user_number;
    String user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend);
        sp = getSharedPreferences(sp_Name, MODE_PRIVATE);
        id = sp.getInt(user_id_sp, 0);
        user_number = sp.getString(phonNumber_sp, "");
        user_name = sp.getString(user_name_sp, "");
        if ((id==0) || (user_number.length()==0) ||(user_name.length()==0))
        {
            gotoHome();
        }
        getContactList();
        //todo php code that check do we have that number in DB (Problems: starts with +code or 00 code or 0)
    }


    private void gotoHome() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    private void getContactList() {
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
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Fiends+=phoneNo.toString().replaceAll(" ","")+",";
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
        Log.i("end", Fiends);
    }
}
