package com.example.yasmeen.advancedchatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
    private List<Conv> mConvs ;
    private List<Users> user__chats ;
    private ChatAdapter mAdapter;
    Users user;
    Message m;
    Conv conv;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat, container, false);
        mChatList =(RecyclerView)mView.findViewById(R.id.chat_list);
        mAuth = FirebaseAuth.getInstance();
        mConvs = new ArrayList<>();
        user__chats = new ArrayList<>();
        current_user = mAuth.getCurrentUser().getUid();
        mConDataBase = FirebaseDatabase.getInstance().getReference().child("Chat").child(current_user);
       // mConDataBase.keepSynced(true);
        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDataBase = FirebaseDatabase.getInstance().getReference().child("Messages").child(current_user);
        mMessageDataBase.keepSynced(true);
        mAdapter = new ChatAdapter(getContext(),user__chats);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mChatList.setLayoutManager(linearLayoutManager);
        mChatList.setItemAnimator(new DefaultItemAnimator());
        mChatList.setAdapter(mAdapter);
        mConvs.clear();
        Query conversationQuery = mConDataBase.orderByChild("timestamp");
        conversationQuery.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                conv = dataSnapshot.getValue(Conv.class);
                String key = dataSnapshot.getKey();
                mConvs.add(conv);
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


        return mView;

    }
    private int hasId(String idc){
        if(!TextUtils.isEmpty(idc)) {
            for (int i=0 ;i< user__chats.size();i++) {
                if (user__chats.get(i).getUid().equals(idc)) {
                    return i ;
                }
            }
        }
        return -1;
    }
    private void retrieve(final String key) {
        mAdapter.notifyDataSetChanged();
        mUserDataBase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Users.class);
                user.setUid(key);
                if(user!=null && hasId(user.getUid()) ==-1)
                {
                    user__chats.add(0,user);
                    mAdapter.notifyDataSetChanged();

                }
                    Query messageQuery =mMessageDataBase.child(key).limitToLast(1);
                messageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot1, String s) {
                        m = dataSnapshot1.getValue(Message.class);
                        int positionm = hasId(dataSnapshot.getKey());
                            user__chats.get(positionm).setStatus(m.getMessage());
                            user=user__chats.get(positionm);
                            user__chats.remove(positionm);
                            user__chats.add(0,user);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        mAdapter.notifyDataSetChanged();
        super.onStart();
    }
}
