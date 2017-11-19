package com.example.davit.redcarpet;


import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class ApiConnector {
    public ApiConnector() {}

    public JSONArray GetProductDetails(int ProductID)
    {
        // URL for getting all customers
        String url = "https://welcome2romania.000webhostapp.com/getProduct.php?ProductID="+ProductID;

        // Get HttpResponse Object from url.
        // Get HttpEntity from Http Response Object

        HttpEntity httpEntity = null;

        try
        {

            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
            HttpGet httpGet = new HttpGet(url);
            //// TODO: 11/18/2017 use only post metod (HttpPost) 


            HttpResponse httpResponse = httpClient.execute(httpGet);

            httpEntity = httpResponse.getEntity();

        } catch (ClientProtocolException e) {

            // Signals error in http protocol
            e.printStackTrace();

            //Log Errors Here



        } catch (IOException e) {
            e.printStackTrace();
        }


        // Convert HttpEntity into JSON Array
        JSONArray jsonArray = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.d("Entity Response  : ", entityResponse);

                jsonArray = new JSONArray(entityResponse);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonArray;
    }

    public String AddParty(String name, String date , String start, String end, String andress, String adresshint, String description, String user_id,String image ) {
       // Party(name, date, start, end, andress, adresshint, description, user_id, image)
        String url = "https://redcarpetproject.000webhostapp.com/AddParty.php";
        HttpEntity httpEntity = null;


        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(9);
            nameValuePair.add(new BasicNameValuePair("name", name));
            nameValuePair.add(new BasicNameValuePair("date", date));
            nameValuePair.add(new BasicNameValuePair("start", start));
            nameValuePair.add(new BasicNameValuePair("end", end));
            nameValuePair.add(new BasicNameValuePair("andress", andress));
            nameValuePair.add(new BasicNameValuePair("adresshint", adresshint));
            nameValuePair.add(new BasicNameValuePair("description", description));
            nameValuePair.add(new BasicNameValuePair("user_id", user_id));
            nameValuePair.add(new BasicNameValuePair("image", image));


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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return entityResponse;
        }

    public String AddProfile(String name, String adres, String info, int id, String image)
    {
        String url = "https://redcarpetproject.000webhostapp.com/AddProfile.php";
        HttpEntity httpEntity = null;


        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
            nameValuePair.add(new BasicNameValuePair("name", name));
            nameValuePair.add(new BasicNameValuePair("adres", adres));
            nameValuePair.add(new BasicNameValuePair("info", info));
            nameValuePair.add(new BasicNameValuePair("image", image));
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

        if (httpEntity != null) {
            try {
                entityResponse = EntityUtils.toString(httpEntity);
                Log.e("Entity Response  : ", entityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entityResponse;
    }

    public String Edit(String am_name, String ru_name, String ro_name, String en_name, String priceAM, String priceRO, String quantity, String type, String data, int id)
    {
        String url = "https://welcome2romania.000webhostapp.com/edit.php?am_name="+am_name+"&ru_name="+ru_name+"&ro_name="+ro_name+"&en_name="+en_name+"&priceAM="+priceAM
                +"&priceRO="+priceRO+"&quantity="+quantity+"&type="+type+"&data="+data+"&id="+id;
        url=url.replaceAll(" ", "_");
        HttpEntity httpEntity = null;

        try
        {

            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            httpEntity = httpResponse.getEntity();

        } catch (ClientProtocolException e) {

            // Signals error in http protocol
            e.printStackTrace();

            //Log Errors Here



        } catch (IOException e) {
            e.printStackTrace();
        }


        // Convert HttpEntity into JSON Array
        String entityResponse=null;

        if (httpEntity != null) {
            try {
                 entityResponse = EntityUtils.toString(httpEntity);

//                Log.e("Entity Response  : ", entityResponse);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entityResponse;

    }

    public String Delete(int id, String data)
    {
        String url = "https://welcome2romania.000webhostapp.com/delete.php?id="+id+"&data="+data;

        HttpEntity httpEntity = null;

        try
        {

            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            httpEntity = httpResponse.getEntity();

        } catch (ClientProtocolException e) {

            // Signals error in http protocol
            e.printStackTrace();

            //Log Errors Here



        } catch (IOException e) {
            e.printStackTrace();
        }


        // Convert HttpEntity into JSON Array
        //JSONArray jsonArray = null;

        String entityResponse=null;
        if (httpEntity != null) {
            try {
                 entityResponse = EntityUtils.toString(httpEntity);

//                Log.e("Entity Response  : ", entityResponse);

               /// jsonArray = new JSONArray(entityResponse);

//            } catch (JSONException e) {
//                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entityResponse;

    }


    public Boolean uploadImageToserver(List<NameValuePair> params) {

        String url = "https://redcarpetproject.000webhostapp.com/uploadImage.php";



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

}

