package com.avinash.homework7;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ViewProfileActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userID;
    EditText editFNAME,editLNAME;
    ImageView editImage;
    Button edit,cancel;
    int img_Code = 101;
    Uri imageUri=Uri.EMPTY;
    String imageURLUpdated;
    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    String firstname,lastname,imageURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        editFNAME = (EditText)findViewById(R.id.editFnameID);
        editLNAME = (EditText)findViewById(R.id.editLnameID);
        editImage = (ImageView)findViewById(R.id.viewProfileImage);
        edit = (Button) findViewById(R.id.editProfileID);
        cancel = (Button) findViewById(R.id.editCancelID);

        userID = getIntent().getExtras().getString("user");

        DatabaseReference userref = ref.child("users").child(userID);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firstname = dataSnapshot.child("firstname").getValue().toString();
                lastname = dataSnapshot.child("lastname").getValue().toString();
                editFNAME.setText(firstname);
                editLNAME.setText(lastname);
                if(dataSnapshot.child("imageURL").getValue().toString()!= null){
                imageURL = dataSnapshot.child("imageURL").getValue().toString();
                    Picasso.with(getApplicationContext()).load(imageURL).fit().centerCrop().into(editImage);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/*");
                startActivityForResult(imageIntent,img_Code);
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!editFNAME.getText().toString().equals("") && !editLNAME.getText().toString().equals("")) {

                    DatabaseReference updateuser = ref.child("users").child(userID);

                    updateuser.child("firstname").setValue(editFNAME.getText().toString());
                    updateuser.child("lastname").setValue(editLNAME.getText().toString());

                    updateuser.child("imageURL").setValue(imageURL);
                    Toast.makeText(ViewProfileActivity.this, "User has been updated", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == img_Code)
            if (resultCode == Activity.RESULT_OK) {

                imageUri = data.getData();
                if(imageUri != null) {
                StorageReference childRef = mStorageReference.child("Photos").child(imageUri.getLastPathSegment());
                childRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        imageURL = taskSnapshot.getDownloadUrl().toString();


                        Toast.makeText(ViewProfileActivity.this,"Upload Successfull",Toast.LENGTH_SHORT).show();
                        Picasso.with(getApplicationContext()).load(taskSnapshot.getDownloadUrl()).fit().centerCrop().into(editImage);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ViewProfileActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();

                    }
                });
                }

            }}


}
