package com.example.davit.redcarpet.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davit.redcarpet.ApiConnector;
import com.example.davit.redcarpet.R;
import com.example.davit.redcarpet.Tools;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {


    TextView Name;
    TextView Adress;
    TextView Info;
    TextView PhonNumber;
    CircleImageView ProfPic;
    private int OrgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);
        Name = (TextView) findViewById(R.id.name);
        Adress = (TextView) findViewById(R.id.adress);
        Info = (TextView) findViewById(R.id.info);
        PhonNumber = (TextView) findViewById(R.id.phon);
        ProfPic = (CircleImageView) findViewById(R.id.prof_pic);
        OrgId = getIntent().getIntExtra("org_id",-1);

        new ViewProfileActivity.GetUserByID().execute(new ApiConnector());
    }

    public void back(View view) {
        finish();
    }

    public void addFriend(View view) {
        Toast.makeText(getApplicationContext(),"Friend added",Toast.LENGTH_SHORT).show();
    }

    private class GetUserByID extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            return params[0].GetUserByID(OrgId);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                fillUserInformation(jsonArray.getJSONObject(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillUserInformation (JSONObject jsonObject) throws JSONException {
        //`id`, `number`, `name`, `adress`, `info`, `image`
        Name.setText(jsonObject.getString("name"));
        Adress.setText(jsonObject.getString("adress"));
        Info.setText(jsonObject.getString("info"));
        PhonNumber.setText(jsonObject.getString("number"));
        String url = Tools.IMAGES_URL + jsonObject.getString("number") + ".jpg";
        Log.e("url", url);
        Picasso.with(this).load(url)
                .placeholder(R.drawable.prof_pic_def)
                .error(R.drawable.prof_pic_def)
                .into(ProfPic);
    }
}
