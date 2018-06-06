package com.example.yasmeen.advancedchatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment {
    private RecyclerView mfrindsList ;
    private DatabaseReference mRetrieveFriendsList , mFriendData;
    private FirebaseAuth mAuth ;
    private String current_user;
    private View mView;
    Friends friend;
    Users user_;
    Users users;
    List<Users> users_friendsData = new ArrayList<>();
    private FriendsAdapter mAdapter;
    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_friends, container, false);
        mfrindsList =(RecyclerView) mView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser().getUid();
        final List<Friends> friends = new ArrayList<>();
        users_friendsData = new ArrayList<>();
        mRetrieveFriendsList = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user);
        mfrindsList.setHasFixedSize(true);
        mfrindsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter=new FriendsAdapter(getContext(),users_friendsData);
        mfrindsList.setAdapter(mAdapter);

        mRetrieveFriendsList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                friend = dataSnapshot.getValue(Friends.class);
                friends.add(friend);
                mFriendData = FirebaseDatabase.getInstance().getReference().child("Users").child(dataSnapshot.getKey());

                mFriendData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        users =dataSnapshot.getValue(Users.class);
                        users.setUid(dataSnapshot.getKey());
                        users.setStatus(friend.getData());
                        users_friendsData.add(users);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
