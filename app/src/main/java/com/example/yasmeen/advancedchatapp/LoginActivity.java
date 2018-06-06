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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;
    private Toolbar mLoginToolbar;
    private Button mLoginButton;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference userTokenRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginToolbar =(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mLoginToolbar);
        getSupportActionBar().setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        userTokenRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mLoginEmail =(TextInputLayout)findViewById(R.id.loginEmail);
        mLoginPassword =(TextInputLayout)findViewById(R.id.loginPassword);

        mAuth = FirebaseAuth.getInstance();

        mLoginButton =(Button)findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= mLoginEmail.getEditText().getText().toString();
                String password= mLoginPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                {
                    mProgressDialog.setTitle(getString(R.string.logging_in));
                    mProgressDialog.setMessage(getString(R.string.please_wait));
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    LoginUser(email,password);
                }
            }
        });
    }

    private void LoginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull  Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            mProgressDialog.dismiss();
                            String deviceTokenID = FirebaseInstanceId.getInstance().getToken();
                            String current_user_id = mAuth.getCurrentUser().getUid();
                            FirebaseUser user = mAuth.getCurrentUser();

                            userTokenRef.child(current_user_id).child("device_token").setValue(deviceTokenID).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent newIntent= new Intent(LoginActivity.this,MainActivity.class);
                                    newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(newIntent);
                                    finish();
                                }
                            });

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                           // Log.w(TAG, "signInWithEmail:failure", task.getException());
                            mProgressDialog.hide();
                            Toast.makeText(LoginActivity.this, R.string.failed_login,
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Email",mLoginEmail.getEditText().getText().toString());
        outState.putString("Password",mLoginPassword.getEditText().getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLoginEmail.getEditText().setText(savedInstanceState.getString("Email"));
        mLoginPassword.getEditText().setText(savedInstanceState.getString("Password"));
    }
}
