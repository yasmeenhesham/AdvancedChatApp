package com.example.yasmeen.advancedchatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private TextView mDisplayName,mStatus , mFriendsNumber;
    private ImageView mImg;
    private Button mFriendRequestBtn, mDeclineBtn;
    private DatabaseReference mDatabaseReferenceFriends , mDatabaseRefFriendsReq , mNotificationRef , mDatabaseRefRoot;
    //private ProgressDialog mProgressDialog;
    private String friend_state;
    private FirebaseUser currrent_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mDisplayName = (TextView)findViewById(R.id.profileName);
        mStatus =(TextView)findViewById(R.id.profile_status);
        mFriendsNumber =(TextView)findViewById(R.id.profileFriends);
        mFriendRequestBtn = (Button)findViewById(R.id.sendFriendRequest);
        mDeclineBtn = (Button)findViewById(R.id.declineRequest);
        mImg =(ImageView)findViewById(R.id.profilImg);
        final Users y = (Users) getIntent().getExtras().getSerializable("User");
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(y.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                y.setStatus(dataSnapshot.child("status").getValue().toString());
                mStatus.setText(y.getStatus());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDisplayName.setText(y.getName());
        mStatus.setText(y.getStatus());
        Picasso.get().load(y.getImage()).placeholder(R.drawable.user_icon).into(mImg);
        mDatabaseRefFriendsReq = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mDatabaseReferenceFriends =FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationRef =FirebaseDatabase.getInstance().getReference().child("Notifications");
        mDatabaseRefRoot = FirebaseDatabase.getInstance().getReference();
        friend_state = "not_friends";
        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);
        currrent_user = FirebaseAuth.getInstance().getCurrentUser();
       /* mProgressDialog = new ProgressDialog(ProfileActivity.this);
        mProgressDialog.setTitle("Loading User data");
        mProgressDialog.setMessage("Please wait a while");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();*/
       /* mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String img = dataSnapshot.child("image").getValue().toString();
                mDisplayName.setText(name);
                mStatus.setText(status);
                Picasso.get().load(img).placeholder(R.drawable.user_icon).into(mImg);
                mProgressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        mDatabaseRefFriendsReq.child(currrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(y.getUid()))
                {
                    String req_type =dataSnapshot.child(y.getUid()).child("request_type").getValue().toString();
                    if(req_type.equals("recieved"))
                    {
                        friend_state="req_received";
                        mFriendRequestBtn.setText(R.string.accept_friend_request);
                        mDeclineBtn.setVisibility(View.VISIBLE);
                        mDeclineBtn.setEnabled(true);
                    }
                    else if(req_type.equals("sent"))
                    {
                        friend_state="req_sent";
                        mFriendRequestBtn.setText(R.string.cancle_friend_request);
                        mDeclineBtn.setVisibility(View.INVISIBLE);
                        mDeclineBtn.setEnabled(false);
                    }
                }
                else {
                    mDatabaseReferenceFriends.child(currrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {

                            if(dataSnapshot1.hasChild(y.getUid()))
                            {
                                mFriendRequestBtn.setEnabled(true);
                                friend_state="friends";
                                mFriendRequestBtn.setText(R.string.unfriend);
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendRequestBtn.setEnabled(false);
                if(friend_state.equals("not_friends"))
                {
                    DatabaseReference mNotificationRef2 = mDatabaseRefRoot.child("Notifications").child(y.getUid()).push();
                    String newNotificationId = mNotificationRef2.getKey();
                    HashMap<String,String> notificationData = new HashMap<>();
                    notificationData.put("from",currrent_user.getUid());
                    notificationData.put("type","request");
                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Req/"+currrent_user.getUid()+"/"+y.getUid()+"/request_type","sent");
                    requestMap.put("Friend_Req/"+y.getUid()+"/"+currrent_user.getUid()+"/request_type","recieved");
                    requestMap.put("Notifications/"+y.getUid()+"/"+newNotificationId,notificationData);
                    mDatabaseRefRoot.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            /*mNotificationRef.child(y.getUid()).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friend_state ="req_sent";
                                    mFriendRequestBtn.setText("Cancel Friend Request");
                                    mFriendRequestBtn.setEnabled(true);
                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);
                                }
                            });*/
                            if(databaseError != null)
                            {

                            }
                            friend_state ="req_sent";
                            mFriendRequestBtn.setEnabled(true);
                            mFriendRequestBtn.setText(R.string.cancel_friend_request);

                        }
                    });
                   /* mDatabaseRefFriendsReq.child(currrent_user.getUid()).child(y.getUid()).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mDatabaseRefFriendsReq.child(y.getUid()).child(currrent_user.getUid()).child("request_type")
                                        .setValue("recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String,String> notificationData = new HashMap<>();
                                        notificationData.put("from",currrent_user.getUid());
                                        notificationData.put("type","request");

                                        mNotificationRef.child(y.getUid()).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                friend_state ="req_sent";
                                                mFriendRequestBtn.setText("Cancel Friend Request");
                                                mFriendRequestBtn.setEnabled(true);
                                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                                mDeclineBtn.setEnabled(false);
                                            }
                                        });


                                       // Toast.makeText(ProfileActivity.this,"Successful send",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(ProfileActivity.this,"task is failed",Toast.LENGTH_LONG).show();

                            }
                        }
                    });*/
                }
                if(friend_state.equals("req_sent"))
                {
                    Map friendaMap = new HashMap();
                    friendaMap.put("Friend_Req/"+currrent_user.getUid()+"/"+y.getUid(), null);
                    friendaMap.put("Friend_Req/"+y.getUid()+"/"+currrent_user.getUid(), null);
                    mDatabaseRefRoot.updateChildren(friendaMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null)
                            {
                                mFriendRequestBtn.setEnabled(true);
                                friend_state="not_friends";
                                mFriendRequestBtn.setText(R.string.send_friend_request);
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }
                        }
                    });

                    mDatabaseRefFriendsReq.child(currrent_user.getUid()).child(y.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseRefFriendsReq.child(y.getUid()).child(currrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendRequestBtn.setEnabled(true);
                                    friend_state="not_friends";
                                    mFriendRequestBtn.setText(R.string.send_friend_request);
                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }
                if(friend_state.equals("req_received"))
                {
                    final String currentDate = DateFormat.getDateInstance().format(new Date());
                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/"+currrent_user.getUid()+"/"+y.getUid()+"/date",currentDate);
                    friendsMap.put("Friends/"+y.getUid()+"/"+currrent_user.getUid()+"/date",currentDate);
                    friendsMap.put("Friend_Req/"+currrent_user.getUid()+"/"+y.getUid(), null);
                    friendsMap.put("Friend_Req/"+y.getUid()+"/"+currrent_user.getUid(), null);
                    mDatabaseRefRoot.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null)
                            {
                                mFriendRequestBtn.setEnabled(true);
                                friend_state="friends";
                                mFriendRequestBtn.setText(R.string.unfriend);
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }
                        }
                    });
                    /*mDatabaseReferenceFriends.child(currrent_user.getUid()).child(y.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseReferenceFriends.child(y.getUid()).child(currrent_user.getUid()).child("date").setValue(currentDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mDatabaseRefFriendsReq.child(currrent_user.getUid()).child(y.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mDatabaseRefFriendsReq.child(y.getUid()).child(currrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mFriendRequestBtn.setEnabled(true);
                                                            friend_state="friends";
                                                            mFriendRequestBtn.setText("Unfriend");
                                                            mDeclineBtn.setVisibility(View.INVISIBLE);
                                                            mDeclineBtn.setEnabled(false);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                        }
                    });*/
                }
                if(friend_state.equals("friends"))
                {
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/"+currrent_user.getUid()+"/"+y.getUid() , null);
                    unfriendMap.put("Friends/"+y.getUid()+"/"+currrent_user.getUid(), null);
                    mDatabaseRefRoot.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null)
                            {
                                mFriendRequestBtn.setEnabled(true);
                                friend_state="not_friends";
                                mFriendRequestBtn.setText(R.string.send_friend_request);
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }
                        }
                    });
                    /*mDatabaseReferenceFriends.child(currrent_user.getUid()).child(y.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseReferenceFriends.child(y.getUid()).child(currrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendRequestBtn.setEnabled(true);
                                    friend_state="not_friends";
                                    mFriendRequestBtn.setText("Send Friend Request");
                                }
                            });
                        }
                    });*/
                }

            }
        });
    }
}
