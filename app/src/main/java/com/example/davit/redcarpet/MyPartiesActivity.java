package com.example.davit.redcarpet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyPartiesActivity extends AppCompatActivity {


    ListView myPartylist;
    private JSONArray jsonArray;

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
        setContentView(R.layout.my_parties);
        sp = getSharedPreferences(sp_Name, MODE_PRIVATE);
        id = sp.getInt(user_id_sp, 0);
        user_number = sp.getString(phonNumber_sp, "");
        user_name = sp.getString(user_name_sp, "");
        if ((id==0) || (user_number.length()==0) ||(user_name.length()==0))
        {
           gotoHome();
        }


        myPartylist= (ListView) findViewById(R.id.mypartylist);
        new MyPartiesActivity.GetMyParties().execute(new ApiConnector());
        this.myPartylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    JSONObject productClicked = jsonArray.getJSONObject(position);
                    Intent showDetails = new Intent(getApplicationContext(), EditMyPartyActivity.class);
                    showDetails.putExtra("PartyID", productClicked.getInt("Id"));
                    startActivity(showDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });
    }

    public void setListAdapter(JSONArray jsonArray)
    {
        Log.d("JSON_object","jsonArray_setListAdapter= "+jsonArray);
        this.jsonArray=jsonArray;
        this.myPartylist.setAdapter(new PartiesAdapter(jsonArray,this));

    }
    private class GetMyParties extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            return params[0].MyParties(id);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setListAdapter(jsonArray);


        }
    }


    private void gotoHome() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
