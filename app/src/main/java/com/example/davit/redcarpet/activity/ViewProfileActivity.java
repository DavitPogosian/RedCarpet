package com.example.davit.redcarpet.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    TextView Name;
    TextView Adress;
    TextView Info;
    TextView PhonNumber;
    CircleImageView ProfPic;
    RatingBar rating;
    private int OrgId;
    private Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);
        Name = (TextView) findViewById(R.id.name);
        Adress = (TextView) findViewById(R.id.adress);
        Info = (TextView) findViewById(R.id.info);
        PhonNumber = (TextView) findViewById(R.id.phon);
        ProfPic = (CircleImageView) findViewById(R.id.prof_pic);
        rating = (RatingBar) findViewById(R.id.ratingBar);


        OrgId = getIntent().getIntExtra("org_id",-1);
        loading = Tools.showLoading(this);
        new ViewProfileActivity.GetUserByID().execute(new ApiConnector());
    }

    public void back(View view) {
        finish();
    }
    public String getFriend() {
        String number=PhonNumber.getText().toString();
        if (!"".equals(number.trim())) {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
            Cursor cur = this.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    String name=cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));
                    return name;
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
        }
        return null;
    }
    public void addFriend(View view) {
        Boolean checkPermission=Tools.checkPermissions(this, new String[]{ Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, Tools.PERMISSION_READ_CONTACTS);
        if (checkPermission!=null && checkPermission)
            addFriendToContact();
    }
    private void addFriendToContact() {
        String friendName=getFriend();
        if (friendName==null) {

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            int rawContactInsertIndex = ops.size();

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(RawContacts.ACCOUNT_TYPE, null)
                    .withValue(RawContacts.ACCOUNT_NAME, null).build());

            //Phone Number
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, PhonNumber.getText().toString())
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "1").build());

            //Display name/Contact name
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, Name.getText().toString())
                    .build());

            try {
                ContentProviderResult[] res = getContentResolver().applyBatch(
                        ContactsContract.AUTHORITY, ops);
                Toast.makeText(getBaseContext(), "Friend added", Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                Toast.makeText(getBaseContext(), "Unable to add Friend " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                Toast.makeText(getBaseContext(), "Unable to add Friend " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getBaseContext(), "Contact already exist (" + friendName+")", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Tools.PERMISSION_READ_CONTACTS: {
                if (Tools.isAllPermissionsGranted(grantResults)) {
                    addFriendToContact();
                } else {
                    Toast.makeText(getBaseContext(),"Permission denied to add Friend ",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private class GetUserByID extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            return params[0].GetUserByID(OrgId);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            loading.cancel();
            try {
                if (jsonArray!=null && jsonArray.length()>0)
                    fillUserInformation(jsonArray.getJSONObject(0));
                else
                    Toast.makeText(getBaseContext(), "Unable to get user information, try again later", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class RateUser extends AsyncTask<ApiConnector,Long,JSONObject> {
        @Override
        protected JSONObject doInBackground(ApiConnector... apiConnectors) {
            Log.e("Rating", String.valueOf(rating.getRating()));
            return apiConnectors[0].rate(OrgId, rating.getRating());
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null && result.has("success") && !result.has("error")) {
                Toast.makeText(getBaseContext(), "Rate recorded, thanks for voting", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getBaseContext(), "Unable to rate, try later", Toast.LENGTH_SHORT).show();
        }
    }

    private void fillUserInformation (JSONObject jsonObject) throws JSONException {
        //`id`, `number`, `name`, `adress`, `info`, `image`
        Name.setText(jsonObject.getString("name"));
        Adress.setText(jsonObject.getString("adress"));
        Info.setText(jsonObject.getString("info"));
        PhonNumber.setText(jsonObject.getString("number"));
        String rate=jsonObject.getString("Rating");
        if (rate != null && rate.matches("\\d*\\.\\d+"))
            rating.setRating((float) jsonObject.getDouble("Rating"));
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean fromUser) {
                Log.e("Change Ratting ", String.valueOf(v)+" / "+ratingBar.getNumStars());
                new ViewProfileActivity.RateUser().execute(new ApiConnector());
            }
        });
        String url = Tools.IMAGES_URL + jsonObject.getString("image") + ".jpg";
        Log.e("url", url);
        Picasso.with(this).load(url)
                .placeholder(R.drawable.prof_pic_def)
                .error(R.drawable.prof_pic_def)
                .into(ProfPic);
    }

}
