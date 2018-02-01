package com.example.davit.redcarpet;


import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class ApiConnector {
    public ApiConnector() {}

    public JSONArray GetAllParties() {
        return getArray(sendHttpGet("GetAllParties.php"));
    }
    public JSONArray GetNextParties() {
        return getArray(sendHttpGet("GetNextParties.php"));
    }

    public JSONArray MyParties(int id) {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("id", String.valueOf(id)));
        return getArray(sendHttpPost("MyParties.php", nameValuePair));
    }

    public JSONArray GetPartyById(String id)
    {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("id", id));
        return getArray(sendHttpPost("GetPartyById.php", nameValuePair));
    }
    public JSONArray ShowPartyById(String id) {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("id", id));
        return getArray(sendHttpPost("ShowPartyById.php", nameValuePair));
    }

    public JSONArray GetPartyByUserId(int id)
    {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("user_id", String.valueOf(id)));
        return getArray(sendHttpPost("GetPartyByUserId.php", nameValuePair));
    }

    public String AddParty(String name, String startDate , String endDate, String andress, String adresshint, String description, String user_id,String image ) {
        // Party(name, start, end, andress, adresshint, description, user_id, image)
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(8);
        nameValuePair.add(new BasicNameValuePair("name", name));
        nameValuePair.add(new BasicNameValuePair("startDate", startDate));
        nameValuePair.add(new BasicNameValuePair("endDate", endDate));
        nameValuePair.add(new BasicNameValuePair("andress", andress));
        nameValuePair.add(new BasicNameValuePair("adresshint", adresshint));
        nameValuePair.add(new BasicNameValuePair("description", description));
        nameValuePair.add(new BasicNameValuePair("user_id", user_id));
        nameValuePair.add(new BasicNameValuePair("image", image));
        return sendHttpPost("AddParty.php", nameValuePair);
    }

    public String AddProfile(String number,String name, String adress, String info, String image)
    {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
        nameValuePair.add(new BasicNameValuePair("number", number));
        nameValuePair.add(new BasicNameValuePair("name", name));
        nameValuePair.add(new BasicNameValuePair("adress", adress));
        nameValuePair.add(new BasicNameValuePair("info", info));
        nameValuePair.add(new BasicNameValuePair("image", image));
        return sendHttpPost("AddProfile.php", nameValuePair, false);
    }
    public String EditProfile(int id,String number,String name, String adress, String info, String image) {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(6);
        nameValuePair.add(new BasicNameValuePair("id", String.valueOf(id)));
        nameValuePair.add(new BasicNameValuePair("number", number));
        nameValuePair.add(new BasicNameValuePair("name", name));
        nameValuePair.add(new BasicNameValuePair("adress", adress));
        nameValuePair.add(new BasicNameValuePair("info", info));
        nameValuePair.add(new BasicNameValuePair("image", image));
        return sendHttpPost("EditProfile.php",nameValuePair);
    }
    public JSONArray GetUserByID(int id)
    {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("id", String.valueOf(id)));
        return getArray(sendHttpPost("GetUserByID.php", nameValuePair));
    }
    public JSONArray GetUserByNumber(String number)
    {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("number", number));
        return getArray(sendHttpPost("GetUserByNumber.php", nameValuePair));
    }

    public String EditParty(String name, String startDate , String endDate, String andress, String adresshint, String description, String user_id,String image, int id ) {
        // Party(name, date, start, end, andress, adresshint, description, user_id, image)
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(10);
        nameValuePair.add(new BasicNameValuePair("name", name));
        nameValuePair.add(new BasicNameValuePair("startDate", startDate));
        nameValuePair.add(new BasicNameValuePair("endDate", endDate));
        nameValuePair.add(new BasicNameValuePair("andress", andress));
        nameValuePair.add(new BasicNameValuePair("adresshint", adresshint));
        nameValuePair.add(new BasicNameValuePair("description", description));
        nameValuePair.add(new BasicNameValuePair("user_id", user_id));
        nameValuePair.add(new BasicNameValuePair("image", image));
        nameValuePair.add(new BasicNameValuePair("id", String.valueOf(id)));
        return sendHttpPost("EditParty.php", nameValuePair);
    }
    public JSONObject sendSMS(String number) {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("phone", number));
        return getObject(sendHttpPost("SendSMS.php", nameValuePair, false));
    }



    public JSONObject checkSMS(String number, String code)  {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("phone", number));
        nameValuePair.add(new BasicNameValuePair("code", code));
        return getObject(sendHttpPost("CheckSMS.php", nameValuePair, false));
    }
    public JSONArray GetRegistredFriends(String friends) {
        List<NameValuePair> nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("friends", friends));
        return getArray(sendHttpPost("GetRegistredFriends.php", nameValuePairs));
    }

    public JSONArray getAttendees(int partyId) {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("partyId", String.valueOf(partyId)));
        return getArray(sendHttpPost("GetPartyAttendees.php", nameValuePair));
    }
    public Boolean attend(int partyId, int status) {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("partyId", String.valueOf(partyId)));
        nameValuePair.add(new BasicNameValuePair("status", String.valueOf(status)));
        String result = sendHttpPost("AttendParty.php", nameValuePair);
        return (result!=null)?"1".equals(result):null;
    }
    public JSONObject rate(int user_id, float rating){
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));
        nameValuePair.add(new BasicNameValuePair("rate", String.valueOf((int) rating)));
        return getObject(sendHttpPost("rate.php", nameValuePair));
    }

    public Boolean uploadImageToserver(List<NameValuePair> params) {
        String result = sendHttpPost("uploadImage.php", params);
        return result != null && !result.contains("error");
    }
    public boolean deleteParty(int party_id) {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("party_id", String.valueOf(party_id)));
        return "1".equals(sendHttpPost("DeleteParty.php",nameValuePair));
    }
    private String sendHttpGet(String uri) {
        return sendHttpGet(uri, true);
    }
    private String sendHttpGet(String uri, boolean addToken) {
        HttpEntity httpEntity = null;
        try {
            Log.i("HttpGET",uri);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Tools.ROOT_URL+uri);
            if (addToken)
                addToken(httpGet);

            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                // write response to log
                Log.d("Http Get Response:", httpResponse.toString());
                httpEntity = httpResponse.getEntity();
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("HttpGet",e.toString());
                // Log exception
                e.printStackTrace();
            }
        } catch (Exception e) {
            // Log exception
            e.printStackTrace();
        }


        String entityResponse = null;

        if (httpEntity != null) {
            try {
                entityResponse = EntityUtils.toString(httpEntity);
                if (entityResponse.startsWith("AUTH_ERROR")) {
                    Log.e("HttpGet", "Bad token "+uri);
                    Tools.invalidateToken();
                }
                Log.e("Entity Response  : ", entityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entityResponse;
    }

    private String sendHttpPost(String uri, List<NameValuePair> nameValuePair) {
        return sendHttpPost(uri, nameValuePair, true);
    }
    private String sendHttpPost(String uri, List<NameValuePair> nameValuePair, boolean addToken) {
        HttpEntity httpEntity = null;
        try {
            Log.i("HttpPost",uri);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Tools.ROOT_URL+uri);
            if (addToken)
                addToken(httpPost);
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                // write response to log
                Log.d("Http Post Response:", httpResponse.toString());
                httpEntity = httpResponse.getEntity();
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }
        } catch (Exception e) {
            // Log exception
            e.printStackTrace();
        }


        String entityResponse = null;

        if (httpEntity != null) {
            try {
                entityResponse = EntityUtils.toString(httpEntity);
                Log.e("Entity Response  : ", entityResponse);
                if (entityResponse.startsWith("AUTH_ERROR")) {
                    Log.e("HttpGet", "Bad token "+uri);
                    Tools.invalidateToken();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entityResponse;
    }

    private void addToken(HttpRequestBase request) {
        request.addHeader("token", Tools.getCurrentId()+"-"+Tools.getCurrentToken());
    }


    private JSONArray getArray(String jsonArray) {
        if (jsonArray != null)
            try {
                return new JSONArray(jsonArray);
            } catch (JSONException e) {
            }
        return null;
    }
    private JSONObject getObject(String s) {
        if (s!=null)
            try {
                return new JSONObject(s);
            } catch (JSONException e) {
                try {
                    return new JSONObject().put("success", false).put("error", "Response parse error:"+e.getMessage());
                } catch (JSONException e1) {
                }
            }
        return null;
    }


}

