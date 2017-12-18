package com.example.davit.redcarpet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewProfileActivity extends AppCompatActivity {


    String Number;
    int id;
    String nameForsp;
    boolean img_is_set;
    String imageData;

    EditText Name;
    EditText Adress;
    EditText Info;
    TextView PhonNumber;
    CircleImageView ProfPic;

    SharedPreferences sp;
    private static final String sp_Name = "userinfo";
    private static final String phonNumber_sp = "Number";
    private static final String user_id_sp = "ID";
    private static final String user_name_sp = "Name";

    private static final String TAG = "NewProfileActivity";
    private static final int MAX_NAME_LENGHT = 30;
    private static final int MAX_ADRESS_LENGHT = 100;
    private static final int MAX_INFO_LENGHT = 200;
    private static final int SELECT_PICTURE = 1;
    private static final int ERROR_MESSAGE_MIN_LENGHT = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_profile);

        setNumber();
        sp = getSharedPreferences(sp_Name, MODE_PRIVATE);
//        Number=sp.getString(phonNumber_sp,"");
//        id=sp.getInt(user_id_sp,0);
//        if((Number.length()==0)||(id==0))
//        {
//            gotomain();
//        }

        Name = (EditText) findViewById(R.id.name);
        Adress = (EditText) findViewById(R.id.adress);
        Info = (EditText) findViewById(R.id.info);
        PhonNumber = (TextView) findViewById(R.id.phon);
        ProfPic = (CircleImageView) findViewById(R.id.prof_pic);

    }

    public void setNumber() {
        if (checkpermission()) {
            try {
                TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                    return;
                }
                String mPhoneNumber = tMgr.getLine1Number();
                Log.e("Number",mPhoneNumber);
                PhonNumber=(TextView) findViewById(R.id.phon);
                PhonNumber.setText(mPhoneNumber);
                Number = mPhoneNumber;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
    public boolean checkpermission()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                return true;
            }
        }
        else {
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public void save(View view)
    {
        if(validation())
        {
            String name= Name.getText().toString();
            String adres=Adress.getText().toString();
            String info=Info.getText().toString();
            nameForsp = Name.getText().toString();
            new AddProfile().execute(new ApiConnector());
        }
    }
    private class AddProfile extends AsyncTask<ApiConnector, Long, String>
    {
        String name= Name.getText().toString();
        String adres=Adress.getText().toString();
        String info=Info.getText().toString();
        @Override
        //protected JSONArray doInBackground(ApiConnector... params)
        protected String doInBackground(ApiConnector... params)
        {
            if(img_is_set)
            return params[0].AddProfile(Number,name, adres, info, Number);

            return params[0].AddProfile(Number,name, adres, info,"def");
        }

        @Override
        //protected void onPostExecute(JSONArray jsonArray)
        protected void onPostExecute(String Result)
        {
            try {
                Log.d(TAG,"Result = "+Result);
                if(success(Result)) {

                    if (img_is_set)
                        uploadImg();
                    else
                    gotohome();
                }else
                {
                    Log.e(TAG,Result);
                    Toast.makeText(getApplicationContext(), "Something goes wrong : Please try again", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.d(TAG, "onPostExecute"+e);
                e.printStackTrace();
            }
        }



    }

    public boolean success(String Result)
    {
        if(Result.length()>ERROR_MESSAGE_MIN_LENGHT)
        {
            return false;
        }
        id=Integer.parseInt(Result);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(phonNumber_sp,Number);
        ed.putInt(user_id_sp,id);
        ed.putString(user_name_sp,nameForsp);
        if(!ed.commit())
        {
            Toast.makeText(getApplicationContext(),"Something goes wrong : Please try again",Toast.LENGTH_SHORT).show();
        }


     return true;
    }
    public boolean validation()
    {
        if(Name.getText().toString().length()==0)
        {
            Log.e(TAG, "validation: Name is null");
            Toast.makeText(getBaseContext(),"Name is mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }else if(Name.getText().toString().length()>MAX_NAME_LENGHT)
        {
            Log.e(TAG, "validation: Name is too big");
            Toast.makeText(getBaseContext(),"Name is depassing maximum lenght ("+MAX_NAME_LENGHT+")", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(Adress.getText().toString().length()>MAX_ADRESS_LENGHT)
        {
            Log.e(TAG, "validation: Adress is too big");
            Toast.makeText(getBaseContext(),"Adress is depassing maximum lenght ("+MAX_ADRESS_LENGHT+")", Toast.LENGTH_SHORT).show();
            return false;
        }else if(Info.getText().toString().length()>MAX_INFO_LENGHT)
        {
            Log.e(TAG, "validation: Description is too big");
            Toast.makeText(getBaseContext(),"Description is depassing maximum lenght ("+MAX_INFO_LENGHT+")", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(Name.getText().toString().length()==0)
        {
            Log.e(TAG, "validation: Number is null");
            Toast.makeText(getBaseContext(),"Something goes wrong : Please try again", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void SetProfPic(View v) {
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
        this.ProfPic.setImageBitmap(image);
        img_is_set=true;

        // upload
        imageData = encodeTobase64(image);


    }


    public void gotohome()
    {
        Intent go = new Intent(this,MainActivity.class);
        startActivity(go);
    }


    public void uploadImg() {
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("image", imageData));
        params.add(new BasicNameValuePair("Name", Number));

        new AsyncTask<ApiConnector, Long, Boolean>() {
            @Override
            protected Boolean doInBackground(ApiConnector... apiConnectors) {
                return apiConnectors[0].uploadImageToserver(params);
            }
            @Override
            protected void onPostExecute(Boolean Result)
            {
                if(Result) {
                    gotohome();
                }else{
                    Toast.makeText(getApplicationContext(), "Something goes wrong : Please try again", Toast.LENGTH_LONG).show();
                }
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

}
