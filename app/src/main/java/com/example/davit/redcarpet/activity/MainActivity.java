package com.example.davit.redcarpet.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davit.redcarpet.ApiConnector;
import com.example.davit.redcarpet.PartiesAdapter;
import com.example.davit.redcarpet.R;
import com.example.davit.redcarpet.Tools;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView Parties;
    private JSONArray jsonArray;

    Context context=this;
    TextView nav_usr_name;

    int id;
    String user_number;
    String user_name;

    CircleImageView pic;

    SharedPreferences sp;
    public static final String sp_Name = "userinfo";
    public static final String phonNumber_sp = "Number";
    public static final String user_id_sp = "ID";
    public static final String user_name_sp = "Name";
    public static final String token = "Token";
    private Dialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        View hView = navView.getHeaderView(0);

        nav_usr_name = (TextView) hView.findViewById(R.id.name);
        pic = (CircleImageView) hView.findViewById(R.id.prof_pic);
        Parties = (ListView) findViewById(R.id.allpartylist);


        sp = getSharedPreferences(sp_Name, MODE_PRIVATE);

        id = sp.getInt(user_id_sp, 0);
        user_number = sp.getString(phonNumber_sp, "");
        user_name = sp.getString(user_name_sp, "");
        Tools.setCurrentId(id);
        Tools.setCurrentToken(sp.getString(token, ""));

//        Tools.setCurrentToken("0b7e575ac99c62d5fae2d5169eb9b88bcd41971c1ba60d06656ba5378597e5c5cd056c957c697d7f");
//        SharedPreferences.Editor ed = sp.edit();
//        ed.putString(phonNumber_sp, "0488443770");
//        ed.putInt(user_id_sp, 4);
//        ed.putString(user_name_sp, "Davited");
//        ed.commit();

        if (user_number.length() == 0 || !Tools.tokenIsValid()) {
            Log.e("number leght", "" + user_number.length());
            showLogin();
            return;
        }
    }

    private void fillInfos() {
        if (id == 0 || user_name.length() == 0) {
            new MainActivity.GetUserByNumber().execute(new ApiConnector());
        }
        nav_usr_name.setText(user_name);
        String url = Tools.IMAGES_URL + user_number + ".jpg";
        Log.e("url", url);
        Picasso.with(this).load(url)
                .placeholder(R.drawable.prof_pic_def)
                .error(R.drawable.prof_pic_def)
                .into(pic);

        loading = Tools.showLoading(this, "Loading parties...");
        new GetAllParties().execute(new ApiConnector());
        this.Parties.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    JSONObject productClicked = jsonArray.getJSONObject(position);
                    Intent showDetails = new Intent(getApplicationContext(), PartyDetailsActivity.class);
                    showDetails.putExtra("PartyID", productClicked.getInt("id"));
                    startActivity(showDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp = getSharedPreferences(sp_Name, MODE_PRIVATE);

        id = sp.getInt(user_id_sp, 0);
        user_number = sp.getString(phonNumber_sp, "");
        user_name = sp.getString(user_name_sp, "");
        Tools.setCurrentId(id);
        Tools.setCurrentToken(sp.getString(token, ""));
        if (user_number.length() == 0 || !Tools.tokenIsValid()) {
            Log.e("number leght", "" + user_number.length());
            showLogin();
        } else
            fillInfos();

    }
    private void showLogin() {
        showDialog(1);
    }

    private class GetUserByNumber extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            return params[0].GetUserByNumber(user_number);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            SetUserInformation(jsonArray);
        }
    }
    public void SetUserInformation(JSONArray jsonArray)
    {
        try {

            //`id`, `number`, `name`, `adress`, `info`, `image`
            JSONObject jsonObject = jsonArray.getJSONObject(0);//or 0

            SharedPreferences.Editor ed = sp.edit();
            ed.putInt(user_id_sp,Integer.valueOf(jsonObject.getString("id")));
            ed.putString(user_name_sp,jsonObject.getString("name"));
            if(!ed.commit())
            {
                Toast.makeText(getApplicationContext(),"Something goes wrong : Please try again",Toast.LENGTH_SHORT).show();
            }else
            {
                finish();
                startActivity(getIntent());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            gotoEditProfile();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            adb.setView(inflater.inflate(R.layout.new_user_dialog, null));
            //adb.setTitle("Delete");
           // adb.setMessage("Are you sure you want to delete this word?");
            adb.setPositiveButton("Sign up", myClickListener);
            adb.setNegativeButton("Log in", myClickListener);
            adb.setIcon(android.R.drawable.ic_dialog_info);
            adb.setCancelable(false);
            return adb.create();
        } return super.onCreateDialog(id);
    }
    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    Intent go = new Intent(getBaseContext(),NewProfileActivity.class);
                    startActivity(go);
                    break;
                case Dialog.BUTTON_NEGATIVE:
                     go = new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(go);
                    break;
            }
        }
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.all_parties) {
            new GetAllParties().execute(new ApiConnector());
        } else if (id == R.id.new_party) {
            Intent go = new Intent(MainActivity.this, AddPartyActivity.class);
            startActivity(go);
        } else if (id == R.id.settings) {
            Intent go = new Intent(MainActivity.this, EditMyProfileActivity.class);
            startActivity(go);
        } else if (id == R.id.my_party) {
            Intent go = new Intent(MainActivity.this, MyPartiesActivity.class);
            startActivity(go);
        } else if (id == R.id.agenda) {
            Intent go = new Intent(MainActivity.this, AgendaActivity.class);
            startActivity(go);
        } else if (id == R.id.friend) {
            Intent go = new Intent(MainActivity.this, FriendActivity.class);
            startActivity(go);
        } else if (id == R.id.chat) {
            Intent go = new Intent(MainActivity.this, ChatActivity.class);
            go.putExtra("room_name","public" );
            startActivity(go);
        } else if (id == R.id.sing_out) {
            SharedPreferences.Editor ed = sp.edit();
            ed.putString(phonNumber_sp,"");
            ed.putInt(user_id_sp,0);
            ed.putString(user_name_sp,"");
            ed.putString(token,"");
            Tools.invalidateToken();
            if(!ed.commit())
            {
                Toast.makeText(getApplicationContext(),"Something goes wrong : Please try again",Toast.LENGTH_SHORT).show();
            }else
                {
                    finish();
                    startActivity(getIntent());
                }


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void gotoEditProfile()
    {
        Intent go = new Intent(this,EditMyProfileActivity.class);
        startActivity(go);
    }
    public void setListAdapter(JSONArray jsonArray)
    {
        Log.d("JSON_object","jsonArray_setListAdapter= "+jsonArray);
        this.jsonArray=jsonArray;
        this.Parties.setAdapter(new PartiesAdapter(jsonArray,this));

    }
    private class GetAllParties extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            return params[0].GetAllParties();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            loading.dismiss();
            if (jsonArray==null) {


                Toast.makeText(getApplicationContext(),"Something goes wrong, please check you connection and try again",Toast.LENGTH_SHORT).show();
            }
            setListAdapter(jsonArray);


        }
    }

}
