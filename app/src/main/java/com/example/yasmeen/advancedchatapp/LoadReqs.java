package com.example.yasmeen.advancedchatapp;

import android.os.AsyncTask;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoadReqs extends AsyncTask<String,Void,List<Users>> {
    String current_userID , chat_userID;
    List<Users>allusers = new ArrayList<>();
    Users users;
    DatabaseReference mReqRef = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
    DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
    @Override
    protected List<Users> doInBackground(String... strings) {
        current_userID = strings[0];
        //chat_userID = strings[1];
        mReqRef.child(current_userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String type = dataSnapshot.child("request_type").getValue().toString();
                String key =dataSnapshot.getKey();
                if(type.equals("recieved"))
                {
                    mUserRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            users = dataSnapshot.getValue(Users.class);
                            allusers.add(users);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return allusers;
    }
}
