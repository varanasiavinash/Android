package com.avinash.homework7;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ChatWindowActivity extends AppCompatActivity {

    ListView msgsListView;
    ImageView send, gallery;
    EditText typeMsg;
    String userKey, currentUserKey;
    ArrayList<Message> msgList;
    ArrayList<String> msgkeyList;
    User user = new User();
    private FirebaseAuth auth;
    private FirebaseUser fUser;
    private DatabaseReference mDatabase;
    private StorageReference mStorageReference;
    private Uri galleryImageUri = null;
    TextView friendName;
    ImageView friendImage;
    String entermsg;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    Date date = new Date();
    SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
    String newDate = sd.format(date);

    String downloadURL;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 120)
        {
            if (resultCode == Activity.RESULT_OK) {
                galleryImageUri = data.getData();


            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        msgsListView = (ListView) findViewById(R.id.msgsListView);
        send = (ImageView) findViewById(R.id.sendImage);
        gallery = (ImageView) findViewById(R.id.uploadImage);
        typeMsg = (EditText) findViewById(R.id.typeMsg);
        msgList = new ArrayList<Message>();
        msgkeyList = new ArrayList<>();
        user = getIntent().getExtras().getParcelable("user");
        userKey = getIntent().getExtras().getString("key");
        mStorageReference = FirebaseStorage.getInstance().getReference();
       // friendName = (TextView) findViewById(R.id.friendID);
        friendImage = (ImageView)findViewById(R.id.friendImage);

        auth = FirebaseAuth.getInstance();
        fUser = auth.getCurrentUser();
ref.child("users").child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setTitle(dataSnapshot.child("firstname").getValue().toString()+" "+dataSnapshot.child("lastname").getValue().toString());
                Picasso.with(getApplicationContext()).load(dataSnapshot.child("imageURL").getValue().toString()).fit().centerCrop().into(friendImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(fUser != null) {

            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Messages");
            mRef.orderByChild("senderName").equalTo(auth.getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        msgkeyList.clear();
        msgList.clear();

        for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
            Message msg = messageSnapshot.getValue(Message.class);
            msg.setMsgID(messageSnapshot.getKey());

            if(msg.getReceiverName().equals(userKey))
            {
                msgList.add(msg);
                msgkeyList.add(messageSnapshot.getKey());
            }

        }
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("Messages").orderByChild("senderName").equalTo(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    Message msg = messageSnapshot.getValue(Message.class);
                    msg.setMsgID(messageSnapshot.getKey());

                    if(msg.getReceiverName().equals(fUser.getUid().toString()))
                    {
                        msgList.add(msg);
                        msgkeyList.add(messageSnapshot.getKey());
                    }

                }

                Collections.sort(msgList);
                MsgsListAdapter msgsListAdapter = new MsgsListAdapter(getApplicationContext(),R.layout.msgs_list_row,msgList);
                msgsListView.setAdapter(msgsListAdapter);

                msgsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                        if(msgList.get(i).getSenderName().toString().equals(fUser.getUid().toString()))
                        {
                            ref.child("Messages").child(msgList.get(i).getMsgID().toString()).removeValue();
                        }
                        else
                        {
                            Toast.makeText(ChatWindowActivity.this, "You Cannot Delete",
                                    Toast.LENGTH_SHORT).show();
                        }

                        return false;
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});

        }



        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                entermsg = typeMsg.getText().toString();
                final Message msg1 = new Message();

                if(!entermsg.equals("")) {

                    msg1.setDate(newDate);
                    msg1.setText(entermsg);
                    msg1.setStatus("false");
                    msg1.setReceiverName(userKey);
                    msg1.setSenderName(fUser.getUid());

                    if(galleryImageUri!= null) {

                        StorageReference childRef = mStorageReference.child("Photos").child(galleryImageUri.getLastPathSegment());
                        childRef.putFile(galleryImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                                downloadURL = taskSnapshot.getDownloadUrl().toString();
                                msg1.setImage(downloadURL);
                                Log.d("image url is ",downloadURL);
                                ref.child("Messages").push().setValue(msg1);
                                typeMsg.setText("");

                                galleryImageUri = Uri.EMPTY;

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChatWindowActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                    else {

                        downloadURL = "";
                        msg1.setImage(downloadURL);
                        ref.child("Messages").push().setValue(msg1);
                        typeMsg.setText("");
                        galleryImageUri = null;
                    }




                }

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/*");
                startActivityForResult(imageIntent,120);

            }
        });
    }
}
