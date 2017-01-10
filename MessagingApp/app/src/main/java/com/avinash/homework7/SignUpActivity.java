package com.avinash.homework7;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SignUpActivity extends AppCompatActivity {

    EditText fname,lname,email,cpwd;
    RadioGroup radioGroup;
    RadioButton b;
    Button signup,cancel;
    int img_Code = 100;
    ImageView loginImage;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    String gender;
    String imageURL;
    Uri imageUri=Uri.EMPTY;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    DatabaseReference ref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        fname = (EditText) findViewById(R.id.signUpFirstName);
        lname = (EditText) findViewById(R.id.signUpLastName);
        email = (EditText) findViewById(R.id.signUpEmailID);
        cpwd = (EditText)findViewById(R.id.signUpChoosePassword);
        signup = (Button)findViewById(R.id.signUpButton);
        cancel = (Button)findViewById(R.id.cancelButton);
        radioGroup = (RadioGroup)findViewById(R.id.genderRG);
        loginImage = (ImageView)  findViewById(R.id.loginImageUpload);

        mAuth = FirebaseAuth.getInstance();


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId()== R.id.radioButtonMale){
                    gender = "Male";
                }
                else if(radioGroup.getCheckedRadioButtonId()== R.id.radioButtonFemale){
                    gender = "Female";
                }
            }
        });

loginImage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageIntent.setType("image/*");
        startActivityForResult(imageIntent,img_Code);
    }
});

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if(!fname.getText().toString().equals("") && !lname.getText().toString().equals("") && !email.getText().toString().equals("") && !cpwd.getText().toString().equals("") && !gender.equals("")) {
                     mAuth.createUserWithEmailAndPassword(email.getText().toString(), cpwd.getText().toString())
                             .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {

                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                     // If sign in fails, display a message to the user. If sign in succeeds
                                     // the auth state listener will be notified and logic to handle the
                                     // signed in user can be handled in the listener.
                                     if (!task.isSuccessful()) {
                                         Toast.makeText(SignUpActivity.this, "The account was not created please select a different email",

                                                 Toast.LENGTH_SHORT).show();
                                     } else {


                                         DatabaseReference usersRef = ref.child("users");

                                         User user = new User(fname.getText().toString(), lname.getText().toString(), email.getText().toString(), gender,imageURL);
                                         usersRef.child(task.getResult().getUser().getUid()).setValue(user);
                                         Toast.makeText(SignUpActivity.this, "User has been created", Toast.LENGTH_SHORT).show();

                                         finish();
                                     }

                                     // [START_EXCLUDE]

                                     // [END_EXCLUDE]
                                 }
                             });

                 }

                 else {
                     Toast.makeText(SignUpActivity.this, "Please enter all the details",
                             Toast.LENGTH_LONG).show();

                 }
            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == img_Code)
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();

                StorageReference childRef = mStorageReference.child("Photos").child(imageUri.getLastPathSegment());
                childRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                      imageURL = taskSnapshot.getDownloadUrl().toString();


                        Toast.makeText(SignUpActivity.this,"Upload Successfull",Toast.LENGTH_SHORT).show();
                        Picasso.with(getApplicationContext()).load(taskSnapshot.getDownloadUrl()).fit().centerCrop().into(loginImage);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SignUpActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();

                    }
                });
            }}

}
