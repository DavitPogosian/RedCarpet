package com.example.davit.redcarpet;


import android.util.Log;
import android.view.WindowManager;

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
    private final static String baseurl=Tools.WEBSERVER;

    public JSONArray GetAllParties() {
        String response = sendHttpGet(baseurl+"GetAllParties.php");

        JSONArray jsonArray = null;

        if (response != null) {
            try {
                Log.d("Entity Response  : ", response);
                jsonArray = new JSONArray(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray;
    }
    public JSONArray GetNextParties() {
        String url = baseurl+"GetNextParties.php";
        try {
            return new JSONArray(sendHttpGet(url));
        } catch (JSONException e) {
            return null;
        }
    }


    public JSONArray MyParties(int id) {
        String url = baseurl + "MyParties.php";
        HttpEntity httpEntity = null;


        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            addToken(httpPost);
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(9);
            nameValuePair.add(new BasicNameValuePair("id", String.valueOf(id)));
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
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                entityResponse = EntityUtils.toString(httpEntity);
                Log.e("Entity Response  : ", entityResponse);
                jsonArray = new JSONArray(entityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray;

    }

    public JSONArray GetPartyById(String id)
    {
        String url = baseurl+"GetPartyById.php";
        HttpEntity httpEntity = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
        nameValuePair.add(new BasicNameValuePair("id", id));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("UnsupportedEncoding",e.toString());
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // write response to log
            Log.d("Http Post Response:", httpResponse.toString());
            httpEntity = httpResponse.getEntity();
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException",e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException",e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Exception",e.toString());
            e.printStackTrace();
        }
        JSONArray jsonArray = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.d("Entity Response  : ", entityResponse);

                jsonArray = new JSONArray(entityResponse);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONException",e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException",e.toString());
            }
        }

        return jsonArray;
    }
    public JSONArray ShowPartyById(String id) {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
        nameValuePair.add(new BasicNameValuePair("id", id));
        String result = sendHttpPost(baseurl+"ShowPartyById.php", nameValuePair);
        JSONArray jsonArray = null;

        if (result!= null) {
            try {
                jsonArray = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONException",e.toString());
            }
        }

        return jsonArray;
    }

    public JSONArray GetPartyByUserId(int id)
    {
        String ID= String.valueOf(id);
        String url = baseurl+"GetPartyByUserId.php";
        HttpEntity httpEntity = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
        nameValuePair.add(new BasicNameValuePair("user_id", ID));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("UnsupportedEncoding",e.toString());
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // write response to log
            Log.d("Http Post Response:", httpResponse.toString());
            httpEntity = httpResponse.getEntity();
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException",e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException",e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Exception",e.toString());
            e.printStackTrace();
        }
        JSONArray jsonArray = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.d("Entity Response  : ", entityResponse);

                jsonArray = new JSONArray(entityResponse);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONException",e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException",e.toString());
            }
        }

        return jsonArray;
    }

    public String AddParty(String name, String startDate , String endDate, String andress, String adresshint, String description, String user_id,String image ) {
       // Party(name, start, end, andress, adresshint, description, user_id, image)
        String url = baseurl+"AddParty.php";
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(9);
            nameValuePair.add(new BasicNameValuePair("name", name));
            nameValuePair.add(new BasicNameValuePair("startDate", startDate));
            nameValuePair.add(new BasicNameValuePair("endDate", endDate));
            nameValuePair.add(new BasicNameValuePair("andress", andress));
            nameValuePair.add(new BasicNameValuePair("adresshint", adresshint));
            nameValuePair.add(new BasicNameValuePair("description", description));
            nameValuePair.add(new BasicNameValuePair("user_id", user_id));
            nameValuePair.add(new BasicNameValuePair("image", image));
            return sendHttpPost(url, nameValuePair);

        }

    public String AddProfile(String number,String name, String adress, String info, String image)
    {
        String url = baseurl+"AddProfile.php";

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
            nameValuePair.add(new BasicNameValuePair("number", number));
            nameValuePair.add(new BasicNameValuePair("name", name));
            nameValuePair.add(new BasicNameValuePair("adress", adress));
            nameValuePair.add(new BasicNameValuePair("info", info));
            nameValuePair.add(new BasicNameValuePair("image", image));
            return sendHttpPost(baseurl+"AddProfile.php", nameValuePair, false);
    }
    public String EditProfile(int id,String number,String name, String adress, String info, String image) {
        String url = baseurl+"EditProfile.php";
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(6);
        nameValuePair.add(new BasicNameValuePair("id", String.valueOf(id)));
        nameValuePair.add(new BasicNameValuePair("number", number));
        nameValuePair.add(new BasicNameValuePair("name", name));
        nameValuePair.add(new BasicNameValuePair("adress", adress));
        nameValuePair.add(new BasicNameValuePair("info", info));
        nameValuePair.add(new BasicNameValuePair("image", image));
        return sendHttpPost(url,nameValuePair);
    }



    public Boolean uploadImageToserver(List<NameValuePair> params) {

        String url = baseurl+ "uploadImage.php";

        HttpEntity httpEntity = null;

        try
        {

            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);

            httpEntity = httpResponse.getEntity();
            String entityResponse = EntityUtils.toString(httpEntity);

            Log.e("Entity Response  : ", entityResponse);
            return true;

        } catch (ClientProtocolException e) {

            // Signals error in http protocol
            e.printStackTrace();

            //Log Errors Her


        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONArray GetUserByID(int id)
    {
        String ID= String.valueOf(id);
        String url = baseurl+"GetUserByID.php";
        HttpEntity httpEntity = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("id", ID));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("UnsupportedEncoding",e.toString());
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // write response to log
            Log.d("Http Post Response:", httpResponse.toString());
            httpEntity = httpResponse.getEntity();
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException",e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException",e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Exception",e.toString());
            e.printStackTrace();
        }
        JSONArray jsonArray = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.d("Entity Response  : ", entityResponse);

                jsonArray = new JSONArray(entityResponse);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONException",e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException",e.toString());
            }
        }

        return jsonArray;
    }
    public JSONArray GetUserByNumber(String number)
    {

        String url = baseurl+"GetUserByNumber.php";
        HttpEntity httpEntity = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
        nameValuePair.add(new BasicNameValuePair("number", number));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("UnsupportedEncoding",e.toString());
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // write response to log
            Log.d("Http Post Response:", httpResponse.toString());
            httpEntity = httpResponse.getEntity();
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException",e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException",e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Exception",e.toString());
            e.printStackTrace();
        }
        JSONArray jsonArray = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.d("Entity Response  : ", entityResponse);

                jsonArray = new JSONArray(entityResponse);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONException",e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException",e.toString());
            }
        }

        return jsonArray;
    }

    public String EditParty(String name, String startDate , String endDate, String andress, String adresshint, String description, String user_id,String image, int id ) {
        // Party(name, date, start, end, andress, adresshint, description, user_id, image)
        String url = baseurl+"EditParty.php";

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


        return sendHttpPost(url, nameValuePair);
    }
    public JSONObject sendSMS(String number) {
        String url = baseurl + "SendSMS.php";
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("phone", number));

        try {
            return new JSONObject(sendHttpPost(url, nameValuePair, false));
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                return new JSONObject().put("success", false).put("error", e.getMessage());
            } catch (JSONException e1) {
                return null;
            }
        }
    }

    public JSONObject checkSMS(String number, String code)  {
        String url = baseurl+"CheckSMS.php";
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("phone", number));
        nameValuePair.add(new BasicNameValuePair("code", code));
        try {
            return new JSONObject(sendHttpPost(url, nameValuePair, false));
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                return new JSONObject().put("success", false).put("error", e.getMessage());
            } catch (JSONException e1) {
                return null;
            }
        }
    }
    public JSONArray GetRegistredFriends(String friends) {
        String url = baseurl+"GetRegistredFriends.php";
        List<NameValuePair> nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("friends", friends));
        try {
            return new JSONArray(sendHttpPost(url, nameValuePairs));
        } catch (JSONException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public JSONArray getAttendees(int partyId) {
        String url = baseurl + "GetPartyAttendees.php";
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("partyId", String.valueOf(partyId)));
        try {
            return new JSONArray(sendHttpPost(url, nameValuePair));
        } catch (JSONException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }
    public Boolean attend(int partyId, int status) {
        String url = baseurl + "AttendParty.php";
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("partyId", String.valueOf(partyId)));
        nameValuePair.add(new BasicNameValuePair("status", String.valueOf(status)));
        String result = sendHttpPost(url, nameValuePair);
        return (result!=null)?"1".equals(result):null;
    }

    private String sendHttpGet(String url) {
        return sendHttpGet(url, true);
    }
    private String sendHttpGet(String url, boolean addToken) {
        HttpEntity httpEntity = null;
        try {
            Log.i("HttpGET",url);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
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
                    Log.e("HttpGet", "Bad token "+url);
                    Tools.invalidateToken();
                }
                Log.e("Entity Response  : ", entityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entityResponse;
    }

    private String sendHttpPost(String url, List<NameValuePair> nameValuePair) {
        return sendHttpPost(url, nameValuePair, true);
    }
    private String sendHttpPost(String url, List<NameValuePair> nameValuePair, boolean addToken) {
        HttpEntity httpEntity = null;
        try {
            Log.i("HttpPost",url);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
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
                    Log.e("HttpGet", "Bad token "+url);
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

}

