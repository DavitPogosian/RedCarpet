package com.example.davit.redcarpet;

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



    SharedPreferences sp;
    String Number;
    int id;
    boolean img_is_set;

    public String imageData;

    EditText Name;
    EditText Adress;
    EditText Info;
    TextView PhonNumber;
    CircleImageView ProfPic;

    private static final  String sp_Name="userinfo";
    private static final String TAG = "NewProfileActivity";
    private static final  String phonNumber_sp="Number";
    private static final  String uder_id_sp="ID";
    private static final int MAX_NAME_LENGHT = 30;
    private static final int MAX_ADRESS_LENGHT = 100;
    private static final int MAX_INFO_LENGHT = 200;
    private static final int SELECT_PICTURE = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_profile);

        sp= getSharedPreferences(sp_Name,MODE_PRIVATE);
        Number=sp.getString(phonNumber_sp,"");
        id=sp.getInt(uder_id_sp,0);
        if((Number.length()==0)||(id==0))
        {
            gotomain();
        }

        Name = (EditText) findViewById(R.id.name);
        Adress = (EditText) findViewById(R.id.adress);
        Info = (EditText) findViewById(R.id.name);
        PhonNumber = (TextView) findViewById(R.id.info);
        ProfPic = (CircleImageView) findViewById(R.id.prof_pic);


        PhonNumber.setText(Number);



    }


    public void gotomain()
    {
        Intent go= new Intent(this,MainActivity.class);
        startActivity(go);
    }


    public void save(View view)
    {
        if(validation())
        {
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
            return params[0].AddProfile(name, adres, info, id, Number);

            return params[0].AddProfile(name, adres, info, id,"");
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
            } catch (Exception e) {
                Log.d("Logul din error", "onPostExecute");
                e.printStackTrace();
            }


        }



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





    public void uploadImg() {
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("image", imageData));
        params.add(new BasicNameValuePair("Name", Number+"_"+Name.getText().toString()));

        new AsyncTask<ApiConnector, Long, Boolean>() {
            @Override
            protected Boolean doInBackground(ApiConnector... apiConnectors) {
                return apiConnectors[0].uploadImageToserver(params);
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
