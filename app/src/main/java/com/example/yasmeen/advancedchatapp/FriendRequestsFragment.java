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


public class FriendRequestsFragment extends Fragment {
    private List<Users>req_list;
    FirebaseAuth mauth;
    DatabaseReference mReqRef ;
    DatabaseReference mUserRef ;
    RecyclerView recyclerView;
    String current_id;
    Users users;
    View reqView;
    List<String>names;
    ReqAdapter mReqAdapter ;
    public FriendRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        reqView= inflater.inflate(R.layout.fragment_friend_requests, container, false);
        req_list= new ArrayList<>();
        recyclerView= (RecyclerView)reqView.findViewById(R.id.req_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        names=new ArrayList<>();
        mReqAdapter=new ReqAdapter(getContext(),req_list,names);
        recyclerView.setAdapter(mReqAdapter);
        mauth = FirebaseAuth.getInstance();
        current_id = mauth.getCurrentUser().getUid();
        mReqRef = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mReqRef.child(current_id).addChildEventListener(new ChildEventListener() {
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
                            users.setUid(dataSnapshot.getKey());
                            names.add(users.getName());
                            if(getActivity()!=null) {
                                UpdateBakingService.startBakingService(getActivity().getApplicationContext()
                                        , names);
                            }
                            req_list.add(users);
                            mReqAdapter.notifyDataSetChanged();

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

       /* try {
            req_list = new LoadReqs().execute(current_id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
        //UpdateReqService.startBakingService(getContext(),req_list);

        return reqView;

    }

}
