package com.example.davit.redcarpet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Davit on 02/12/2017.
 */
public class PartiesAdapter extends BaseAdapter {
    private static final String  startImageUrl="https://redcarpetproject.000webhostapp.com/images/";
    private JSONArray dataArray;
    private Activity activity;
    public static LayoutInflater inflater = null;
    public PartiesAdapter(JSONArray jsonArray, Activity a)
    {
        this.dataArray=jsonArray;
        this.activity=a;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        try{
            return this.dataArray.length();
        }
        catch(Exception e){

            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ListParty party;

        if(convertView == null)
        {
            convertView=inflater.inflate(R.layout.party_item,null);
            party = new ListParty();
            party.name=(TextView) convertView.findViewById(R.id.Name);
            party.adress=(TextView) convertView.findViewById(R.id.adress);
            party.End=(TextView) convertView.findViewById(R.id.End);
            party.Start=(TextView) convertView.findViewById(R.id.Start);
            party.party_img=(CircleImageView) convertView.findViewById(R.id.party_image);
            convertView.setTag(party);

        }
        else
        {
            party= (ListParty) convertView.getTag();

        }
        try {
            //`Id`, `name`, `date`, `start`, `end`, `andress`, `adresshint`, `description`, `user_id`, `image`
            JSONObject jsonObject = this.dataArray.getJSONObject(position);
            party.name.setText(jsonObject.getString("name"));
            party.adress.setText(jsonObject.getString("andress"));
            party.End.setText(jsonObject.getString("end"));
            party.Start.setText(jsonObject.getString("start"));
            String imageName=jsonObject.getString("image");
            String fullUrlForimg=startImageUrl+imageName+".jpg";
            Picasso.with(activity).load(fullUrlForimg)
                    .placeholder(R.drawable.party_def_ic)
                    .error(R.drawable.party_def_ic)
                    .into(party.party_img);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ListParty
    {

        private TextView name;
        private TextView adress;
        private TextView End;
        private TextView Start;
        private CircleImageView party_img;
    }















}