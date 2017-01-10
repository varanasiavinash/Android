package com.avinash.homework7;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatScreenActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userID,firstname,lastname,fullname;
    private FirebaseAuth mAuth;
    List<User> userList;
    ArrayList<String> keyList;
    ListView userListView;
    User user1 = null;
    GoogleApiClient mGoogleApiClient;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.search) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();


            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    FirebaseAuth.getInstance().signOut();
                }
            });

            LoginManager.getInstance().logOut();

            finish();
            return true;
        }
        else if(item.getItemId() == R.id.profile){

            Intent intent = new Intent(ChatScreenActivity.this,ViewProfileActivity.class);
            intent.putExtra("user",FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient= new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();


        userListView = (ListView) findViewById(R.id.userListView);

        userID = getIntent().getExtras().getString("user");


        DatabaseReference userref = ref.child("users").child(userID);



        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firstname = dataSnapshot.child("firstname").getValue().toString();
                lastname = dataSnapshot.child("lastname").getValue().toString();
                fullname =  firstname +" "+ lastname;
                setTitle(fullname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference userlistRef = ref.child("users");

        userlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               userList = new ArrayList<User>();
                keyList = new ArrayList<String>();
                userList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if(!postSnapshot.getKey().equals(userID)) {
                        User user = postSnapshot.getValue(User.class);
                        userList.add(user);
                        keyList.add(postSnapshot.getKey());
                    }
                }

                if(userList.size()>0){
                    UsersCustomAdapter usersCustomAdapter = new UsersCustomAdapter(getApplicationContext(),R.layout.users_row_item_layout,userList);
                    userListView.setAdapter(usersCustomAdapter);
                    userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent chatWindow = new Intent(ChatScreenActivity.this,ChatWindowActivity.class);
                            chatWindow.putExtra("user",userList.get(i));
                            chatWindow.putExtra("key",keyList.get(i));
                            startActivity(chatWindow);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
