package com.example.davit.redcarpet;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.davit.redcarpet.activity.AttendeeActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends BaseAdapter {
    private static final String startImageUrl = Tools.IMAGES_URL;
    private JSONArray dataArray;
    private Activity activity;
    private int layoutId;
    public static LayoutInflater inflater = null;

    public UserAdapter(JSONArray jsonArray, Activity a) {
        this(jsonArray, a,R.layout.user_item);
    }
    public UserAdapter(JSONArray jsonArray, Activity a, int layoutId)
    {
        this.layoutId = layoutId;
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
        final User user;

        if(convertView == null)
        {
            convertView=inflater.inflate(layoutId,null);
            user = new User();
            user.name=(TextView) convertView.findViewById(R.id.Name);
            user.image = (CircleImageView) convertView.findViewById(R.id.party_image);
            if (layoutId!=R.layout.attend_item) {
                user.adress = (TextView) convertView.findViewById(R.id.adress);
                user.info = (TextView) convertView.findViewById(R.id.info);
            } else
                user.attend=(TextView)  convertView.findViewById(R.id.attend);
            convertView.setTag(user);

        }
        else
        {
            user= (User) convertView.getTag();

        }
        try {
            //`id`, `name`, `number`, `adress`, `info`, `image`
            JSONObject jsonObject = this.dataArray.getJSONObject(position);
            user.name.setText(jsonObject.getString("name"));
            if (layoutId!=R.layout.attend_item) {
                user.adress.setText(jsonObject.getString("adress"));
                user.info.setText(jsonObject.getString("info"));
            } else
                user.attend.setText(new String(Tools.status[jsonObject.getInt("status")]));
            String imageName=jsonObject.getString("image");
            String fullUrlForimg=startImageUrl+imageName+".jpg";
            Picasso.with(activity).load(fullUrlForimg)
                    .placeholder(R.drawable.party_def_ic)
                    .error(R.drawable.party_def_ic)
                    .into(user.image);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class User
    {

        private TextView name;
        private TextView adress;
        private TextView phone;
        private TextView info;
        private CircleImageView image;
        private TextView attend;
    }

}