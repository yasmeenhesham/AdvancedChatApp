package com.example.yasmeen.advancedchatapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private Users mChatUser;
    private Toolbar chatToolbar;
    private TextView custom_barName , custom_barLast;
    private ImageView custome_img;
    private DatabaseReference mRoot;
    private FirebaseAuth mauth;
    private String mcurrentUserId;
    private EditText sentmessage;
    private StorageReference mStorageRef;
    private ImageButton sendMsgBtn , addBtn;
    private RecyclerView messagesList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private List<Message>all_messages = new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    Message m;
    private static final int LIMITATION_MSG=10;
    private int current_page =1;
    private int item_Positin =0;
    private String mLastKey ="";
    private String mPrevKey="";
    private static final int GALLERY_PICK=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatUser =(Users) getIntent().getExtras().getSerializable("User");
        chatToolbar = (Toolbar)findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(mChatUser.getName());
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar_view = inflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(actionbar_view);
        custom_barName = (TextView)findViewById(R.id.custom_bar_name);
        custom_barLast = (TextView)findViewById(R.id.custom_bar_last_seen);
        custome_img = (ImageView)findViewById(R.id.custom_bar_img);
        sendMsgBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        addBtn =(ImageButton)findViewById(R.id.chat_add_btn);
        sentmessage =(EditText)findViewById(R.id.chat_message);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        messagesAdapter = new MessagesAdapter(all_messages);
        messagesList=(RecyclerView)findViewById(R.id.messages_list);
        messagesList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        messagesList.setLayoutManager(linearLayoutManager);
        messagesList.setAdapter(messagesAdapter);
        mSwipeRefreshLayout =(SwipeRefreshLayout)findViewById(R.id.mesage_swip_layout) ;


        mRoot = FirebaseDatabase.getInstance().getReference();
        mauth = FirebaseAuth.getInstance();
        mcurrentUserId = mauth.getCurrentUser().getUid();
        custom_barName.setText(mChatUser.getName());
        Picasso.get().load(mChatUser.getImage()).placeholder(R.drawable.user_icon).into(custome_img);
        mRoot.child("Users").child(mChatUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                if(online.equals("true"))
                {
                    custom_barLast.setText("online");
                }
                else
                {
                   // String time = dataSnapshot.child("lastSeen").toString()
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long timeactive =Long.parseLong(dataSnapshot.child("lastSeen").getValue().toString());
                    String ago = getTimeAgo.getTimeAgo(timeactive,getApplicationContext());
                    custom_barLast.setText(ago);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRoot.child("Chat").child(mcurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(mChatUser.getUid()))
                {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp",ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/"+mcurrentUserId+"/"+mChatUser.getUid(),chatAddMap);
                    chatUserMap.put("Chat/"+mChatUser.getUid()+"/"+mcurrentUserId,chatAddMap);
                    mRoot.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError!=null) {
                                Log.d("Chat_LOG", databaseError.getMessage().toString());
                            }

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        loadMessages();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                current_page++;
                item_Positin =0;
                loadMoreMessages();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallaryIntent = new Intent();
                gallaryIntent.setType("image/*");
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallaryIntent,"Select Image"),GALLERY_PICK);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Msg",sentmessage.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sentmessage.setText(savedInstanceState.getString("Msg"));
    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = mRoot.child("Messages").child(mcurrentUserId).child(mChatUser.getUid());
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(item_Positin==1)
                {
                    mLastKey = dataSnapshot.getKey().toString();
                }
                m = dataSnapshot.getValue(Message.class);
                all_messages.add(item_Positin++,m);
                messagesAdapter.notifyDataSetChanged();
                messagesList.scrollToPosition(all_messages.size()-1);
                mSwipeRefreshLayout.setRefreshing(false);

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



    private void loadMessages()
    {
        DatabaseReference messageRef = mRoot.child("Messages").child(mcurrentUserId).child(mChatUser.getUid());
        Query messageQuery = messageRef.limitToLast(current_page*LIMITATION_MSG);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                m = dataSnapshot.getValue(Message.class);

                String messageKey= dataSnapshot.getKey();
                item_Positin++;
                if(!mPrevKey.equals(messageKey)) {
                    {
                            all_messages.add(m);
                    }
                } else {
                    mPrevKey =messageKey;
                }

                if(item_Positin==1)
                {
                    mLastKey = dataSnapshot.getKey().toString();
                    mPrevKey = dataSnapshot.getKey().toString();
                }


                messagesAdapter.notifyDataSetChanged();
                //messagesList.scrollToPosition(all_messages.size()-1);
                mSwipeRefreshLayout.setRefreshing(false);
                linearLayoutManager.scrollToPositionWithOffset(10,0);
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

    private void sendMessage() {
        String msg = sentmessage.getText().toString();
        if(!TextUtils.isEmpty(msg))
        {
            String cuurentUser_ref_msg = "Messages/"+mcurrentUserId+"/"+mChatUser.getUid();
            String chatUser_ref_msg = "Messages/"+mChatUser.getUid()+"/"+mcurrentUserId;
            DatabaseReference msg_push_ref= mRoot.child("Messages").child(mcurrentUserId).child(mChatUser.getUid()).push();

            String push_id =msg_push_ref.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message",msg);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mcurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(cuurentUser_ref_msg+"/"+push_id,messageMap);
            messageUserMap.put(chatUser_ref_msg+"/"+push_id,messageMap);
            sentmessage.setText("");
            mRoot.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError!=null)
                    {
                        Log.d("Chat_LOG",databaseError.getMessage().toString() );

                    }
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK)
        {
            Uri imageUri =data.getData();
            final String current_user_ref = "Messages/"+mcurrentUserId+"/"+mChatUser.getUid();
            final String chat_user_ref = "Messages/"+mChatUser.getUid()+"/"+mcurrentUserId;
            DatabaseReference msg_push_ref= mRoot.child("Messages").child(mcurrentUserId).child(mChatUser.getUid()).push();
            final String push_id =msg_push_ref.getKey();

            StorageReference file_path =mStorageRef.child("message_images").child(push_id+".jpg");
            file_path.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        String download_uri=task.getResult().getDownloadUrl().toString();

                        Map messageMap = new HashMap();
                        messageMap.put("message",download_uri);
                        messageMap.put("seen",false);
                        messageMap.put("type","image");
                        messageMap.put("time",ServerValue.TIMESTAMP);
                        messageMap.put("from",mcurrentUserId);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                        messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

                        mRoot.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null)
                                {
                                    Log.d("Chat_LOG",databaseError.getMessage().toString() );

                                }
                            }
                        });
                    }
                }
            });

        }
    }
}
