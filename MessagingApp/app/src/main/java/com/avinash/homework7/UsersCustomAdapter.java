package com.avinash.homework7;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jduvvu on 11/19/16.
 */
public class UsersCustomAdapter extends ArrayAdapter<User>{

    List<User> mData;
    Context mContext;
    int mResource;

    public UsersCustomAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.mData = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
        }
        convertView.setId(position);

        TextView userName = (TextView) convertView.findViewById(R.id.rowName);
        ImageView userPhoto = (ImageView) convertView.findViewById(R.id.rowImage);
        User user = mData.get(position);

        userName.setText(user.getFirstname()+" "+user.getLastname());

            Picasso.with(mContext).load(user.getImageURL()).into(userPhoto);


        return convertView;
    }
}



