package com.example.davit.redcarpet.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.davit.redcarpet.ApiConnector;
import com.example.davit.redcarpet.PartiesAdapter;
import com.example.davit.redcarpet.R;
import com.example.davit.redcarpet.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AgendaActivity extends AppCompatActivity {

    ListView nextPartylist;
    JSONArray nextParties=new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda);
        nextPartylist = findViewById(R.id.nextPartylist);

        new AgendaActivity.GetNextParties().execute(new ApiConnector());

        nextPartylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    JSONObject productClicked = nextParties.getJSONObject(position);
                    Intent showDetails = new Intent(getApplicationContext(), PartyDetailsActivity.class);
                    showDetails.putExtra("PartyID", productClicked.getInt("id"));
                    startActivity(showDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }});
    }
    public void setListAdapter(JSONArray jsonArray)
    {
        Log.d("JSON_object","jsonArray_setListAdapter= "+jsonArray);
        this.nextParties = jsonArray;
        this.nextPartylist.setAdapter(new PartiesAdapter(jsonArray,this));

    }
    private class GetNextParties extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(ApiConnector... apiConnectors) {
                return apiConnectors[0].GetNextParties();
        }
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (jsonArray == null) {
                if (!Tools.tokenIsValid()) {
                    Toast.makeText(getApplicationContext(), "You are disconnected", Toast.LENGTH_LONG).show();
                    finish();
                } else
                    Toast.makeText(getBaseContext(), "Unable to get agenda. please try again later.", Toast.LENGTH_SHORT).show();
            } else if (jsonArray.length() == 0)
                Toast.makeText(getBaseContext(), "No next party found.", Toast.LENGTH_SHORT).show();
            setListAdapter(jsonArray);
        }
    }
    protected void showMessage(String message) {

    }

}

