package com.example.yasmeen.advancedchatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private UsersAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        mToolbar = (Toolbar)findViewById(R.id.allusers_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final List<Users> lst = new ArrayList<>();
        mRecyclerView = (RecyclerView)findViewById(R.id.alluser_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new UsersAdapter(this,lst);
        mRecyclerView.setAdapter(mAdapter);



        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                users =dataSnapshot.getValue(Users.class);
                users.setUid(dataSnapshot.getKey().toString());
                lst.add(users);
                mAdapter.notifyDataSetChanged();
                Log.e("A", "onChildAdded: "+ users.getName() );
                Toast.makeText(AllUsersActivity.this, ""+users.getImage(), Toast.LENGTH_SHORT).show();
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
    protected void onStart() {
        super.onStart();
       // startListening();

    }
/*    public void startListening(){
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToLast(50);

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @Override
            protected void onBindViewHolder(UserViewHolder holder, int position, Users model) {
                // Bind the Chat object to the ChatHolder
                holder.SetUser(model.name,model.status,model.image);
                final String user_id = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(AllUsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);
                    }
                });

                // ...
            }

        };
       // adapter.startListening();
       // mRecyclerView.setAdapter(adapter);
    }*/

   /* public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void SetUser(String name ,String status,String img){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_name);
            userNameView.setText(name);
            TextView userStateView = (TextView) mView.findViewById(R.id.user_status);
            userStateView.setText(status);
            ImageView userImage = (ImageView) mView.findViewById(R.id.user_image);
            Picasso.get().load(img).placeholder(R.drawable.user_icon).into(userImage);


        }
    }*/


}
