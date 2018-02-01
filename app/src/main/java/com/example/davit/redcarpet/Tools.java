package com.example.davit.redcarpet;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Tools {
    public final static String TAG = "Tools";
    public final static String ROOT_URL = "https://redcarpetproject.000webhostapp.com/";//"http://10.0.3.1/www/RedCarpet/";
    public final static String IMAGES_URL = ROOT_URL + "images/";
    // request Id for premission
    public static final int PERMISSION_READ_PHONE = 1;
    public static final int PERMISSION_RECEIVE_SMS = 2;
    public static final int PERMISSION_SEND_SMS = 3;
    public static final int PERMISSION_READ_CONTACTS=4;

    public static String currentToken = "";
    public static int currentId = 0;
    private static boolean tokenIsValid = true;


    public static final String WILL_GO="Will go";
    public static final String GOING="Going";
    public static final String CHECK_IN="Check IN";
    public static final String CHECK_OUT="Check OUT";
    public static final String GONE="Gone";
    public static String[] status= {
            "NO_ATTEND",
            WILL_GO,
            GOING,
            CHECK_IN,
            CHECK_OUT,
            GONE
    };
    public static int getStatusForLabel(String label) {
        for (int i=0;i<status.length;i++) {
            if (status[i].equals(label))
                return i;
        }
        return 0;
    }
    public static Dialog showLoading(Activity app) {
        return showLoading(app, "Loading. Please wait...");
    }

    public static Dialog showLoading(Activity app, String message) {
        ProgressDialog dialog = new ProgressDialog(app);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;

    }

    public static String getNumber(Activity app) {
        try {
            TelephonyManager tMgr = (TelephonyManager) app.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            //android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS
            Boolean check = checkPermission(app, Manifest.permission.READ_PHONE_NUMBERS, PERMISSION_READ_PHONE);
            if (check !=null && check) {
                return tMgr.getLine1Number();
            } else
                return null;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }
    public static Boolean checkPermission(Activity app, String permission, int requestCode)  {
        return checkPermissions(app, new String[]{ permission },requestCode);
    }

    public static Boolean checkPermissions(Activity app, String[] permissions, int requestCode)
    {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsToRequest=new ArrayList();
            for (String permission:permissions) {
                if (app.checkSelfPermission(permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(app, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), requestCode);
                return null;
            } else
                return true;
        }
        else {
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }



    /* Date tools */

    public static Calendar getDateFrom(String formatedDate) {
        Calendar resultDate=null;
        String[] tmp=formatedDate.split(" ");
        if (tmp.length==2) {
            String[] date=tmp[0].split("-");
            String[] time=tmp[1].split(":");
            resultDate=Calendar.getInstance();
            resultDate.set(Integer.parseInt(date[0]), Integer.parseInt(date[1])-1,Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]));
        }
        return resultDate;
    }
    public static String formatDate(Calendar calendar) {
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);
        return String.format("%4d-%02d-%02d %02d:%02d",year,month,dayOfMonth,calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE));
    }
    public static String formatDate(String systemDate) {
        // remember to update pickDate, getDateFrom and DateTimeValidator if you change format
        return systemDate.substring(0,systemDate.length()-3);// just remove seconds for now
    }
    public static void pickDate(final EditText currentDate, Activity activity) {
        final Dialog dialog = new Dialog(activity);

        dialog.setContentView(R.layout.date_time);
        dialog.setTitle("Choose date");
        dialog.findViewById(R.id.btnConfirm);
        final DatePicker datePicker=dialog.findViewById(R.id.date_picker);
        final TimePicker timePicker= dialog.findViewById(R.id.time_picker);

        Calendar calendarDate = getDateFrom(currentDate.getText().toString());

        if (calendarDate!=null) {
            datePicker.updateDate(calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH), calendarDate.get(Calendar.DAY_OF_MONTH));
            timePicker.setCurrentHour(calendarDate.get(Calendar.HOUR)); timePicker.setCurrentMinute(Calendar.MINUTE);
        }
        dialog.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate.setText(String.format("%4d-%02d-%02d %02d:%02d",
                        datePicker.getYear(), (datePicker.getMonth()+1),datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
                dialog.hide();
            }
        });
        dialog.show();
    }

    public static void setCurrentToken(String token) {
        currentToken = token;
    }
    public static String getCurrentToken() {
        return currentToken;
    }

    public static int getCurrentId() { return currentId;  }

    public static void setCurrentId(int currentId) { Tools.currentId = currentId; }

    public static boolean tokenIsValid() {
        return tokenIsValid;
    }
    public static void invalidateToken() {
        tokenIsValid= false;
    }
    public static void validateToken() {
        tokenIsValid = true;
    }

    public static boolean isAllPermissionsGranted(int[] grantResults) {
        boolean accepted=true;
        for (int i=0;i<grantResults.length;i++) {
            accepted=accepted && grantResults[i]==PackageManager.PERMISSION_GRANTED;
        }
        return accepted;
    }
}
