package com.example.yasmeen.advancedchatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {
    private RecyclerView mChatList ;
    private DatabaseReference mConDataBase, mUserDataBase,mMessageDataBase;
    private FirebaseAuth mAuth ;
    private String current_user;
    private View mView;
    private List<Conv> mConvs = new ArrayList<>();
    private List<Users> user__chats = new ArrayList<>();
    private ChatAdapter mAdapter;
    Users user;
    Message m;
    Conv conv;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat, container, false);
        mChatList =(RecyclerView)mView.findViewById(R.id.chat_list);
        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser().getUid();
        mConDataBase = FirebaseDatabase.getInstance().getReference().child("Chat").child(current_user);
       // mConDataBase.keepSynced(true);
        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDataBase = FirebaseDatabase.getInstance().getReference().child("Messages").child(current_user);
        mMessageDataBase.keepSynced(true);
        mAdapter = new ChatAdapter(getContext(),user__chats);
       /// LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
       // linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mChatList.setLayoutManager(linearLayoutManager);
        mChatList.setItemAnimator(new DefaultItemAnimator());
        mChatList.setAdapter(mAdapter);
        Query conversationQuery = mConDataBase.orderByChild("timestamp");
        conversationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                conv = dataSnapshot.getValue(Conv.class);
                String key = dataSnapshot.getKey();
                retrieve(key);

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

       /* retrivedata(new Firebasecall() {
            @Override
            public void Callba(String key) {
                Toast.makeText(getContext(), ""+key, Toast.LENGTH_SHORT).show();
                mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users");
                mUserDataBase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user=dataSnapshot.getValue(Users.class);
                        Log.e("", "onDataChange: "+user.getStatus()+user.getName() );
                        user__chats.add(user);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), ""+user.getName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });*/

        return mView;

    }

    private void retrieve(final String key) {
        mUserDataBase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Users.class);
                user.setUid(key);
                Query messageQuery =mMessageDataBase.child(key).limitToLast(1);
                messageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot1, String s) {
                        m = dataSnapshot1.getValue(Message.class);
                        user.setStatus(m.getMessage());
                        user__chats.add(user);
                        mAdapter.notifyDataSetChanged();
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /*private interface Firebasecall {
        void Callba(String  key);
    }*/
    /*public void retrivedata(final Firebasecall o){
        Query conversationQuery = mConDataBase.orderByChild("timestamp");
        conversationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                conv = dataSnapshot.getValue(Conv.class);
                mConvs.add(conv);
                retrieveChat(dataSnapshot.getKey());
                mAdapter.notifyDataSetChanged();
                Log.e("", "onCh: "+dataSnapshot.getKey() );
                o.Callba(dataSnapshot.getKey());
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

    }*/




   /* private void retrieveChat(final String key) {
     *//*   Log.e("", "retrie: "+key );
        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDataBase.orderByChild(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    user=data.getValue(Users.class);
                    Log.e("", "onDataChange: "+user.getStatus()+user.getName() );
                }

                    /// and you added r here to list

//                mMessageDataBase = FirebaseDatabase.getInstance().getReference().child("Messages").child(current_user);
//                Query messageQuery =mMessageDataBase.child(key).limitToLast(1);
//                messageQuery.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                        user.setStatus(dataSnapshot.child("message").getValue().toString()); /// here chnge status to last message
//                        user__chats.add(user);
//                        Log.e("", "onChildAd: "+user.getStatus() );
//                        mAdapter.notifyDataSetChanged();
//
//
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); *//*
    }*/

    @Override
    public void onStart() {
        super.onStart();
    }
}
