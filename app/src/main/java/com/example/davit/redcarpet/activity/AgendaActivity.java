package com.example.davit.redcarpet.activity;

import android.os.AsyncTask;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.davit.redcarpet.ApiConnector;
import com.example.davit.redcarpet.PartiesAdapter;
import com.example.davit.redcarpet.R;

import org.json.JSONArray;

import java.util.concurrent.Executor;

import static com.example.davit.redcarpet.activity.MainActivity.phonNumber_sp;
import static com.example.davit.redcarpet.activity.MainActivity.user_id_sp;
import static com.example.davit.redcarpet.activity.MainActivity.user_name_sp;

public class AgendaActivity extends AppCompatActivity {

    ListView nextPartylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda);
        nextPartylist = findViewById(R.id.nextPartylist);

        new AgendaActivity.GetNextParties().execute(new ApiConnector());
    }
    public void setListAdapter(JSONArray jsonArray)
    {
        Log.d("JSON_object","jsonArray_setListAdapter= "+jsonArray);
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
            if (jsonArray==null)
                Toast.makeText(getBaseContext(), "Unable to get agenda. please try again later.", Toast.LENGTH_SHORT).show();
            else if (jsonArray.length()==0)
                Toast.makeText(getBaseContext(), "No next party found.", Toast.LENGTH_SHORT).show();

            setListAdapter(jsonArray);
        }
    }
}
