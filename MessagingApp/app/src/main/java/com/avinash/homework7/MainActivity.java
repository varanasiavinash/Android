package com.avinash.homework7;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    Button signUp,signIn;

    EditText email,pwd;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EmailPassword";
    SignInButton googleSignIn;
    User user = new User();
    CallbackManager mCallbackManager;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN=001;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    LoginButton fbLogin;
    //StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    DatabaseReference ref = database.getReference();



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN)
        {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){

            GoogleSignInAccount acct=result.getSignInAccount();
            user.setFirstname(acct.getGivenName());
            user.setLastname(acct.getFamilyName());
            user.setEmail(acct.getEmail());
            if(acct.getPhotoUrl() != null){
                user.setImageURL(acct.getPhotoUrl().toString());
            }

            signInWithGoogle(acct);
        }
    }


    private void signInWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Google Login Failed",Toast.LENGTH_SHORT).show();
                        }else {

                            ref.child("users").child(task.getResult().getUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild("email")) {
                                        DatabaseReference newRef = ref.child("users").child(task.getResult().getUser().getUid());
                                        newRef.setValue(user);
                                        Toast.makeText(MainActivity.this, " SignIn is successful ", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, ChatScreenActivity.class);
                                        intent.putExtra("user",task.getResult().getUser().getUid());
                                        startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(MainActivity.this, ChatScreenActivity.class);
                                        intent.putExtra("user",task.getResult().getUser().getUid());
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        signUp = (Button)findViewById(R.id.createButton);
        signIn = (Button)findViewById(R.id.loginButton);
        googleSignIn = (SignInButton) findViewById(R.id.google_sign_in_button);
        fbLogin = (LoginButton)findViewById(R.id.facebook_sign);
        email = (EditText)findViewById(R.id.loginEmailID);
        pwd = (EditText)findViewById(R.id.loginPassword);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = null;
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null)
        {
            Intent intent = new Intent(MainActivity.this, ChatScreenActivity.class);
            intent.putExtra("user", user.getUid());
            startActivity(intent);
            finish();
        }

        else {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleApiClient= new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();


            googleSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent,RC_SIGN_IN);
                }
            });
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!email.getText().toString().equals("") && !pwd.getText().toString().equals("")) {
                        mAuth.signInWithEmailAndPassword(email.getText().toString(), pwd.getText().toString())
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                        if (!task.isSuccessful()) {
                                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                                            Toast.makeText(MainActivity.this, "Login was not successfull.Please enter valid credentials",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {

                                            Intent intent = new Intent(MainActivity.this, ChatScreenActivity.class);
                                            intent.putExtra("user", task.getResult().getUser().getUid());
                                            startActivity(intent);
                                            finish();
                                        }

                                    }
                                });

                    } else {
                        Toast.makeText(MainActivity.this, "Please enter the details",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        mCallbackManager = CallbackManager.Factory.create();

        fbLogin.setReadPermissions("email","public_profile");
        fbLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                FacebookLogin(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });


    }

    private void FacebookLogin(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Facebook Login Failed",Toast.LENGTH_SHORT).show();
                        }else {

                            final FirebaseUser mUser = mAuth.getCurrentUser();
                            ref.child("users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild("email")) {
                                        Profile myprofile = Profile.getCurrentProfile();
                                        DatabaseReference newRef = ref.child("users").child(mUser.getUid());
                                        User user = new User(myprofile.getFirstName(),myprofile.getLastName(),"","",myprofile.getProfilePictureUri(20,20).toString());
                                        newRef.setValue(user);
                                        Toast.makeText(MainActivity.this, " SignIn is successful ", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, ChatScreenActivity.class);
                                        intent.putExtra("user",task.getResult().getUser().getUid());
                                        startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(MainActivity.this, ChatScreenActivity.class);
                                        intent.putExtra("user",task.getResult().getUser().getUid());
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
