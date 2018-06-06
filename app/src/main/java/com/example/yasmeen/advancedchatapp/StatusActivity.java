package com.example.yasmeen.advancedchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mButton;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mToolbar =(Toolbar)findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent sendIntent = getIntent();
        String oldStatus = sendIntent.getStringExtra("Status");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = mCurrentUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);

        mProgressDialog = new ProgressDialog(StatusActivity.this);

        mStatus = (TextInputLayout)findViewById(R.id.status_change_textview);
        mStatus.getEditText().setText(oldStatus);

        mButton = (Button)findViewById(R.id.statusSave_Change);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setTitle("Saving Changes");
                mProgressDialog.setMessage("Please Wait A Moment");
                mProgressDialog.show();

                String newStatus= mStatus.getEditText().getText().toString();
                mDatabaseReference.child("status").setValue(newStatus).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    mProgressDialog.dismiss();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), R.string.status_error_saving,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
            }
        });

    }
}
