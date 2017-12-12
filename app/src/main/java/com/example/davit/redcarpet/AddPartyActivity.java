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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPartyActivity extends AppCompatActivity {

    EditText Name;
    EditText Date;
    EditText StartTime;
    EditText EndTime;
    EditText Adress;
    EditText AdressHInt;
    EditText Description;
    CircleImageView PartyImage;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    public String imageData;
    boolean img_is_set=false;

    String Number;
    String user_id;

    SharedPreferences sp;

    private static final int SELECT_PICTURE = 1;
    private static final int MAX_NAME_LENGHT = 70;
    private static final int MAX_Adress_LENGHT = 100;
    private static final int MAX_AdressHInt_LENGHT = 200;
    private static final int MAX_Description_LENGHT = 500;
    private static final String TAG = "AddPartyActivity";
    private static final  String phonNumber_sp="Number";
    private static final  String uder_id_sp="ID";
    private static final  String sp_Name="userinfo";


    // TODO: 12/12/2017 add animation while user will white , finish animation before goHOme()
    // TODO: 12/12/2017 the user can not have 2 or more parties with the same name
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_party);
        Name = (EditText) findViewById(R.id.Name);
        Date = (EditText) findViewById(R.id.date);
        StartTime = (EditText) findViewById(R.id.timestart);
        EndTime = (EditText) findViewById(R.id.timeend);
        Adress = (EditText) findViewById(R.id.adress_of);
        AdressHInt = (EditText) findViewById(R.id.adress_hint);
        Description = (EditText) findViewById(R.id.description);
        PartyImage = (CircleImageView) findViewById(R.id.party_img);

        setDate();
        sp= getSharedPreferences(sp_Name,MODE_PRIVATE);
        Number=sp.getString(phonNumber_sp,"");
        user_id=String.valueOf(sp.getInt(uder_id_sp,0));
    }

    public void setDate()
    {
        Date CurrentDate = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(CurrentDate);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        String date=dayOfMonth+"."+month+"."+year;
        Date.setText(date);
        Log.e("Date"," "+date);
    }

    public void add(View view)
    {
        if(validation())
        {
            new AddParty().execute(new ApiConnector());

        }
        Log.e(TAG, "add: "+ validation() );
    }

    private class AddParty extends AsyncTask<ApiConnector, Long, String>
    {
        String name= Name.getText().toString();
        String date=Date.getText().toString();
        String start=StartTime.getText().toString();
        String end=EndTime.getText().toString();
        String andress=Adress.getText().toString();
        String adresshint=AdressHInt.getText().toString();
        String description=Description.getText().toString();
        String image=Number+"_"+Name.getText().toString();
        @Override
        //protected JSONArray doInBackground(ApiConnector... params)
        protected String doInBackground(ApiConnector... params)
        {
            return params[0].AddParty(name, date, start, end, andress, adresshint, description, user_id, image);
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
                createChatRoom();
            } catch (Exception e) {
                Log.d("Logul din error", "onPostExecute");
                e.printStackTrace();
            }


        }



    }
    public boolean validation()
    {
        DateValidator dateValidator= new DateValidator();
        Time24HoursValidator time24HoursValidator= new Time24HoursValidator();

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
        else if(StartTime.getText().toString().length()==0)
        {
            Log.e(TAG, "validation: StartTime is null");
            Toast.makeText(getBaseContext(),"Start Time is mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(EndTime.getText().toString().length()==0)
        {
            Log.e(TAG, "validation: EndTime is null");
            Toast.makeText(getBaseContext(),"End Time is mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(Date.getText().toString().length()==0)
        {
            Log.e(TAG, "validation: Date is null");
            Toast.makeText(getBaseContext(),"Date is mandatory", Toast.LENGTH_SHORT).show();
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

        Log.e(TAG,""+dateValidator.validate(Date.getText().toString())+" "+Date.getText().toString());
        if(!dateValidator.validate(Date.getText().toString()))
        {
            Log.e(TAG, "validation: Date is invalid");
            Toast.makeText(getBaseContext(),"invalid date \n date must be in dd.mm.yyyy format", Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.e(TAG,""+ time24HoursValidator.validate(StartTime.getText().toString())+" "+StartTime.getText().toString());
        if(!time24HoursValidator.validate(StartTime.getText().toString()))
        {
            Log.e(TAG, "validation: StartTime is invalid");
            Toast.makeText(getBaseContext(),"invalid Start Time \n Time must be hh:mm format", Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.e(TAG,""+ time24HoursValidator.validate(EndTime.getText().toString())+" "+EndTime.getText().toString());
        if(!time24HoursValidator.validate(EndTime.getText().toString()))
        {
            Log.e(TAG, "validation: End Time is invalid");
            Toast.makeText(getBaseContext(),"invalid End Time \n Time must be hh:mm format", Toast.LENGTH_SHORT).show();
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

    public void createChatRoom ()
    {

        Map<String,Object> map = new HashMap<String, Object>();
        map.put(Number+"_"+Name.getText().toString(),"");
        root.updateChildren(map);
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
                gohome();
            }
        }.execute(new ApiConnector());

    }

    private void gohome() {
        Intent go = new Intent(this, MainActivity.class);
        startActivity(go);
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
