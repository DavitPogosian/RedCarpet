package com.example.davit.redcarpet.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.davit.redcarpet.ApiConnector;
import com.example.davit.redcarpet.DateTimeValidator;
import com.example.davit.redcarpet.R;
import com.example.davit.redcarpet.Tools;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditMyPartyActivity extends AppCompatActivity {

    int Party_id;
    EditText Name;
    EditText startDate;
    EditText endDate;
    EditText Adress;
    EditText AdressHInt;
    EditText Description;
    CircleImageView PartyImage;

    public String imageData;
    boolean img_is_set=false;
    String Number;
    String user_id;
    private static final String TAG = "EditMyPartyActivity";

    private static final int SELECT_PICTURE = 1;
    private static final int MAX_NAME_LENGHT = 70;
    private static final int MAX_Adress_LENGHT = 100;
    private static final int MAX_AdressHInt_LENGHT = 200;
    private static final int MAX_Description_LENGHT = 500;



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
        setContentView(R.layout.edit_my_party);
        Party_id = getIntent().getIntExtra("PartyID",-1);
        sp = getSharedPreferences(sp_Name, MODE_PRIVATE);
        id = sp.getInt(user_id_sp, 0);
        user_number = sp.getString(phonNumber_sp, "");
        user_name = sp.getString(user_name_sp, "");
        if ((id==0) || (user_number.length()==0) ||(user_name.length()==0))
        {
            gotoHome();
        }

        Name = (EditText) findViewById(R.id.Name);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        Adress = (EditText) findViewById(R.id.adress_of);
        AdressHInt = (EditText) findViewById(R.id.adress_hint);
        Description = (EditText) findViewById(R.id.description);
        PartyImage = (CircleImageView) findViewById(R.id.party_img);

        sp= getSharedPreferences(sp_Name,MODE_PRIVATE);
        Number=sp.getString(phonNumber_sp,"");
        user_id=String.valueOf(sp.getInt(user_id_sp,0));

        setpraty();

    }

    private void setpraty() {
        new EditMyPartyActivity.GetPartyByID().execute(new ApiConnector());
    }

    public void save(View view)
    {
        if(validation())
        {
            new EditMyPartyActivity.EditParty().execute(new ApiConnector());

        }
        Log.e(TAG, "add: "+ validation() );
    }

    public void pickStartDateTime(View view) {
        Tools.pickDate(startDate, this);
    }
    public void pickEndDateTime(View view) {
        Tools.pickDate(endDate, this);
    }


    private class EditParty extends AsyncTask<ApiConnector, Long, String>
    {
        String name= Name.getText().toString();
        String beginDate= startDate.getText().toString();
        String finishDate=endDate.getText().toString();
        String andress=Adress.getText().toString();
        String adresshint=AdressHInt.getText().toString();
        String description=Description.getText().toString();
        String image=Number+"_"+Name.getText().toString();
        @Override
        //protected JSONArray doInBackground(ApiConnector... params)
        protected String doInBackground(ApiConnector... params)
        {
            return params[0].EditParty(name, beginDate, finishDate, andress, adresshint, description, user_id, image,Party_id);
        }

        @Override
        //protected void onPostExecute(JSONArray jsonArray)
        protected void onPostExecute(String jsonArray)
        {

            try {
                String  Result=jsonArray;
                Log.d("Result","Result = "+Result);
                if(img_is_set)
                    uploadImg();
                else gotoHome();

            } catch (Exception e) {
                Log.d("Logul din error", "onPostExecute");
                e.printStackTrace();
            }


        }



    }
    private void gotoHome() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void delete(View view) {
    }
    public boolean validation()
    {
        DateTimeValidator dateTimeValidator= new DateTimeValidator();

        if(Name.getText().toString().length()==0)
        {
            Log.e(TAG, "validation: Name is null");
            Toast.makeText(getBaseContext(),"Name is mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(Adress.getText().toString().length()==0)
        {
            Log.e(TAG, "validation: Adress is null");
            Toast.makeText(getBaseContext(),"Adress is mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(startDate.getText().toString().length()==0)
        {
            Log.e(TAG, "validation: startDate is null");
            Toast.makeText(getBaseContext(),"startDate is mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(endDate.getText().toString().length()==0)
        {
            Log.e(TAG, "validation: startDate is null");
            Toast.makeText(getBaseContext(),"endDate is mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!img_is_set)
        {
            Log.e(TAG, "validation: image is null");
            Toast.makeText(getBaseContext(),"Image is mandatory\nTo set Image click on party icon", Toast.LENGTH_SHORT).show();
            return false;

        }else if(Name.getText().toString().length()>MAX_NAME_LENGHT)
        {
            Log.e(TAG, "validation: Name is too big");
            Toast.makeText(getBaseContext(),"Name is depassing maximum lenght ("+MAX_NAME_LENGHT+")", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(Adress.getText().toString().length()>MAX_Adress_LENGHT)
        {
            Log.e(TAG, "validation: Adress is too big");
            Toast.makeText(getBaseContext(),"Adress is depassing maximum lenght ("+MAX_Adress_LENGHT+")", Toast.LENGTH_SHORT).show();
            return false;
        } else if(AdressHInt.getText().toString().length()>MAX_AdressHInt_LENGHT)
        {
            Log.e(TAG, "validation: Adress hint is too big");
            Toast.makeText(getBaseContext(),"Adress hint is depassing maximum lenght ("+MAX_AdressHInt_LENGHT+")", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(Description.getText().toString().length()>MAX_Description_LENGHT)
        {
            Log.e(TAG, "validation: Description is too big");
            Toast.makeText(getBaseContext(),"Description is depassing maximum lenght ("+MAX_Description_LENGHT+")", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.e(TAG,""+dateTimeValidator.validate(startDate.getText().toString())+" "+ startDate.getText().toString());
        if(!dateTimeValidator.validate(startDate.getText().toString()))
        {
            Log.e(TAG, "validation: startDate is invalid");
            Toast.makeText(getBaseContext(),"invalid date \n date must be in dd.mm.yyyy hh:mm format", Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.e(TAG,""+dateTimeValidator.validate(endDate.getText().toString())+" "+ startDate.getText().toString());
        if(!dateTimeValidator.validate(endDate.getText().toString()))
        {
            Log.e(TAG, "validation: endDate is invalid");
            Toast.makeText(getBaseContext(),"invalid end date \n date must be in dd.mm.yyyy hh:mm format", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void SetPartyPic(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (Build.VERSION.SDK_INT < 19) {
                    String selectedImagePath = getPath(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                    SetImage(bitmap);
                } else {
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        parcelFileDescriptor.close();
                        SetImage(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public String getPath(Uri uri) {
        if( uri == null ) {
            return null;
        }
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }
    private void SetImage(Bitmap image) {
        this.PartyImage.setImageBitmap(image);
        img_is_set=true;

        // upload
        imageData = encodeTobase64(image);


    }





    public void uploadImg() {
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("image", imageData));
        params.add(new BasicNameValuePair("Name", Number+"_"+Name.getText().toString()));

        new AsyncTask<ApiConnector, Long, Boolean>() {
            @Override
            protected Boolean doInBackground(ApiConnector... apiConnectors) {
                return apiConnectors[0].uploadImageToserver(params);
            }
            @Override
            protected void onPostExecute(Boolean result)
            {
                gotoHome();
            }
        }.execute(new ApiConnector());

    }


    public static String encodeTobase64(Bitmap image) {
        System.gc();

        if (image == null)return null;

        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] b = baos.toByteArray();

        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }


    private class GetPartyByID extends AsyncTask<ApiConnector, Long, JSONArray>
    {
        private static final String  startImageUrl= Tools.IMAGES_URL;

        @Override
        //protected JSONArray doInBackground(ApiConnector... params)
        protected JSONArray doInBackground(ApiConnector... params)
        {
            return params[0].GetPartyById(String.valueOf(Party_id));
        }

        @Override
        //protected void onPostExecute(JSONArray jsonArray)
        protected void onPostExecute(JSONArray jsonArray)
        {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if ( !user_id.equals(jsonObject.getString("user_id")))
                {
                    Log.e("curent user!= user","current = "+user_id+" original= "+jsonObject.getString("user_id"));
                    gotoHome();
                }
                //(`Id`, `name`, `date`, `start`, `end`, `andress`, `adresshint`, `description`, `user_id`, `image`
                Name.setText(jsonObject.getString("name"));
                startDate.setText(Tools.formatDate(jsonObject.getString("startDate")));
                endDate.setText(Tools.formatDate(jsonObject.getString("endDate")));
                Adress.setText(jsonObject.getString("andress"));
                AdressHInt.setText(jsonObject.getString("adresshint"));
                Description.setText(jsonObject.getString("description"));
                String imageName=jsonObject.getString("image");
                String fullUrlForimg=startImageUrl+imageName+".jpg";
                Picasso.with(getApplicationContext()).load(fullUrlForimg)
                        .placeholder(R.drawable.party_def_ic)
                        .error(R.drawable.party_def_ic)
                        .into(PartyImage);
                img_is_set=true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
