package com.example.yasmeen.advancedchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    Button mregistButton;
    private FirebaseAuth mAuth;
    Toolbar regToolBar;
    private ProgressDialog progressDialog;
    private DatabaseReference mDataRefereienc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDisplayName =findViewById(R.id.regDisplayName);
        mEmail=findViewById(R.id.regEmail);
        mPassword=findViewById(R.id.regPassword);
        mregistButton =findViewById(R.id.RegButton);
        mAuth = FirebaseAuth.getInstance();
        mregistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password =mPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(name)|| !TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password))
                {
                    progressDialog.setTitle(getString(R.string.register_user));
                    progressDialog.setMessage(getString(R.string.wait_create_account));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    Register_New_User(name, email, password);

                }
            }
        });
        regToolBar =findViewById(R.id.register_toolbar);
        setSupportActionBar(regToolBar);
        getSupportActionBar().setTitle(getResources().getString(R.string.create_account));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
    }

    private void Register_New_User(final String name, String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(TAG, "createUserWithEmail:success");

                        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String Uid= mFirebaseUser.getUid();
                        mDataRefereienc = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
                        String deviceTokenID = FirebaseInstanceId.getInstance().getToken();
                        HashMap<String ,String> userMap = new HashMap<>();
                        userMap.put("name",name);
                        userMap.put("status","Hi there , i am using Advanced Chat App");
                        userMap.put("image","default");
                        userMap.put("thumb_image","default");
                        userMap.put("device_token",deviceTokenID);
                        mDataRefereienc.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    Intent newIntent= new Intent(RegisterActivity.this,MainActivity.class);
                                    newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(newIntent);
                                    finish();
                                }
                            }
                        });


                    } else {
                        // If sign in fails, display a message to the user.
                       // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        progressDialog.hide();
                        Toast.makeText(RegisterActivity.this, R.string.failed_signup,
                                Toast.LENGTH_LONG).show();
                    }

                    // ...
                }
            });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Name",mDisplayName.getEditText().getText().toString());
        outState.putString("Email",mEmail.getEditText().getText().toString());
        outState.putString("Password",mPassword.getEditText().getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDisplayName.getEditText().setText(savedInstanceState.getString("Name"));
        mEmail.getEditText().setText(savedInstanceState.getString("Email"));
        mPassword.getEditText().setText(savedInstanceState.getString("Password"));
    }
}
