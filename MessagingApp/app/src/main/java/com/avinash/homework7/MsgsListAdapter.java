package com.avinash.homework7;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jduvvu on 11/22/16.
 */
public class MsgsListAdapter extends ArrayAdapter<Message> {

    List<Message> mData;
    Context mContext;
    int mResource;
    private FirebaseAuth auth;
    private FirebaseUser fUser;


    public MsgsListAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.mData = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
        }
        convertView.setId(position);
        auth = FirebaseAuth.getInstance();
        fUser = auth.getCurrentUser();

        TextView msgText = (TextView) convertView.findViewById(R.id.msgText);
        TextView timeStamp = (TextView) convertView.findViewById(R.id.time);
        //TextView senderName = (TextView) convertView.findViewById(R.id.senderName);
        ImageView imagemsg = (ImageView)convertView.findViewById(R.id.msgImage);
        Message msg = mData.get(position);
         msgText.setText(msg.getText());
        //senderName.setText(msg.getSenderName());
        PrettyTime pt = new PrettyTime();
        timeStamp.setText(pt.format(new Date(msg.getDate())));

        if(msg.getImage() == null || msg.getImage().equals(""))
        {
            imagemsg.setVisibility(View.GONE);
        }
        else {
            imagemsg.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(msg.getImage()).into(imagemsg);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,100);
        params.weight = 1.0f;

        if(msg.getSenderName().equals(fUser.getUid().toString()))
        {
            msgText.setGravity(Gravity.RIGHT);
            timeStamp.setGravity(Gravity.RIGHT);
            params.gravity = Gravity.RIGHT;
            convertView.setBackgroundColor(Color.GREEN);
        }
        else
        {
            msgText.setGravity(Gravity.LEFT);
            timeStamp.setGravity(Gravity.LEFT);
            params.gravity = Gravity.LEFT;
            convertView.setBackgroundColor(Color.WHITE);
        }

        imagemsg.setLayoutParams(params);



        //todo pretty time

        return convertView;
    }
}