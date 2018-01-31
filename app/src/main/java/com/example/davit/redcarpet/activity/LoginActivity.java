package com.example.davit.redcarpet.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davit.redcarpet.ApiConnector;
import com.example.davit.redcarpet.R;
import com.example.davit.redcarpet.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {


    EditText phone;
    EditText code;
    TextView error;
    Button button;
    int rando;
    String originNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        rando=(int) ((Math.random()*19900)+10000);
        phone = (EditText) findViewById(R.id.number);
        code = (EditText) findViewById(R.id.code);
        error = (TextView) findViewById(R.id.error);
        button = (Button) findViewById(R.id.button7);
        phone.setText(Tools.getNumber(this));
    }


    public void send(View v)
    {
        if (button.getText().toString().equals("SEND SMS")) {
            String Number = phone.getText().toString();

            if (Number.length() == 0) {
                Toast.makeText(getBaseContext(), "Number is mandatory", Toast.LENGTH_SHORT).show();
            } else {
                if (IdInDataBase()) {
                    error.setVisibility(View.GONE);
                    checkMyPermission();
                    originNumber=Number;
                    sendSMS(Number, rando);
                    code.setVisibility(View.VISIBLE);
                    button.setText("VERIFY");
                } else {
                    error.setVisibility(View.VISIBLE);
                }


            }


        }
        else
        {
           checkSMS();
        }
    }
    public boolean IdInDataBase()
    {
        // TODO: 11/20/2017 verify if exist in DB
        return  true;
    }
    public boolean checkMyPermission(){

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission"," SEND_SMS Permission is granted");
            } else {
                Log.v("Permission","SEND_SMS Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
                return false;
            }
            if (checkSelfPermission(android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission"," RECEIVE_SMS Permission is granted");
            } else {
                Log.v("Permission","RECEIVE_SMS Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
                return false;
            }
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission"," READ_PHONE_STATE Permission is granted");
            } else {
                Log.v("Permission","READ_PHONE_STATE Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                return false;
            }

        }
        else {
            Log.v("Permission","Permissions are granted");
        }


        return true;
    }


    private void sendSMS(final String phoneNumber, int message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";



        /* using internet <-> sms gateway
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        */
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getBaseContext(), "SMS RECEIVED",
                        Toast.LENGTH_SHORT).show();

                Bundle extras = intent.getExtras();
                if (extras == null)
                    return;

                Object[] pdus = (Object[]) extras.get("pdus");
                SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[0]);
                String origNumber = msg.getOriginatingAddress();
                String msgBody = msg.getMessageBody();
                code.setText(msgBody);
                checkSMS();
                // Now one can just match the msgBody with the expected
                // confirmation code for example.
            }
        }, intentFilter);

        new AsyncTask<ApiConnector, Long, JSONObject>() {
            @Override
            protected JSONObject doInBackground(ApiConnector... apiConnectors) {
                return apiConnectors[0].sendSMS(originNumber);
            }
            @Override
            protected void onPostExecute(JSONObject object)
            {
                boolean result = false;
                try {
                    result = object.getBoolean("success");
                    if (result) {
                        Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getBaseContext(), "SMS send failed", Toast.LENGTH_SHORT).show();
            }

        }.execute(new ApiConnector());
        /*
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, String.valueOf(message), sentPI, deliveredPI);
        */

    }

    private void checkSMS() {
        final String codeReceived = code.getText().toString();
        new AsyncTask<ApiConnector, Long, JSONObject>() {
            @Override
            protected JSONObject doInBackground(ApiConnector... apiConnectors) {
                return apiConnectors[0].checkSMS(originNumber, codeReceived);
            }
            @Override
            protected void onPostExecute(JSONObject object)
            {
                boolean result = false;
                try {
                    result = object.getBoolean("success");
                    if (result) {
                        JSONObject user = object.getJSONObject("user");
                        setResult(200);
                        registerLogin(originNumber, user.getInt("id"),user.getString("number"), user.getString("name"), object.getString("token"));
                        Toast.makeText(getBaseContext(), "Successfully verified", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getBaseContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }

        }.execute(new ApiConnector());


    }


    private int registerLogin(String originNumber, int id, String number, String name, String token) {
        SharedPreferences sp = getSharedPreferences(MainActivity.sp_Name, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(MainActivity.user_id_sp, id);
        ed.putString(MainActivity.phonNumber_sp, number);
        ed.putString(MainActivity.user_name_sp, name);
        ed.putString(MainActivity.token, token);
        Tools.setCurrentToken(token);
        Tools.setCurrentId(id);
        Tools.validateToken();
        ed.commit();
        return 10;
    }

}
