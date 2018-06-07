package com.example.yasmeen.advancedchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

public class SettingActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseUser currentUser;
    private TextView displayName , statusTextview;
    private ImageView displayImge;
    private Button changeStatusButton , changeImgButton;
    private static final int GALLARY_PICK = 1;
    private StorageReference mStorageRef;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        displayName =(TextView)findViewById(R.id.setting_Name);
        statusTextview = (TextView)findViewById(R.id.setting_status);
        displayImge = (ImageView)findViewById(R.id.setting_image);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid= currentUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        mDatabaseReference.keepSynced(true);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                displayName.setText(name);
                statusTextview.setText(status);
                if(!image.equals("default")) {
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user_icon).into(displayImge, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.user_icon).into(displayImge);


                        }
                    });

                    //Picasso.get().load(image).placeholder(R.drawable.user_icon).into(displayImge);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changeStatusButton =(Button)findViewById(R.id.statusButton);
        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mstatus= statusTextview.getText().toString();
                Intent changeStatusIntent = new Intent(SettingActivity.this,StatusActivity.class);
                changeStatusIntent.putExtra("Status",mstatus);
                startActivity(changeStatusIntent);
            }
        });
        changeImgButton = (Button)findViewById(R.id.imageButton);
        changeImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent gallaryIntent = new Intent();
                gallaryIntent.setType("image/*");
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallaryIntent,getString(R.string.select_img)),GALLARY_PICK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data );
        if(requestCode == GALLARY_PICK && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(SettingActivity.this);
                mProgressDialog.setTitle(getString(R.string.uploding_img));
                mProgressDialog.setMessage(getString(R.string.please_wait_amoment));
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();
                String Uid= currentUser.getUid();
                StorageReference filePath = mStorageRef.child("profile_images").child(Uid+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            String download_url = task.getResult().getDownloadUrl().toString();
                            mDatabaseReference.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, R.string.uploaded_success,Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(SettingActivity.this, R.string.img_uploading_error,Toast.LENGTH_LONG).show();

                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

 }
