package com.example.yasmeen.advancedchatapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReqAdapter  extends RecyclerView.Adapter<ReqAdapter.ReqViewHolder>{
    private static List<Users> users= new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private Users  y;
    private FirebaseAuth mAuth;
    private DatabaseReference mReq_Ref ;
    private DatabaseReference mFriend_Ref;
    private String current_user ;

    public ReqAdapter(Context mContext, List<Users>m) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.users = m;
        mAuth = FirebaseAuth.getInstance();
        mReq_Ref = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriend_Ref = FirebaseDatabase.getInstance().getReference().child("Friends");
        current_user= mAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.friend_req_layout, parent, false);
        ReqViewHolder viewHolder = new ReqViewHolder(view);
        return viewHolder;    }

    @Override
    public void onBindViewHolder(@NonNull ReqViewHolder holder, int position) {
        y=users.get(position);
        holder.SetUser(y);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ReqViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.req_name)
        TextView name;
        @BindView(R.id.req_status) TextView status;
        @BindView(R.id.req_image)
        ImageView img;
        @BindView(R.id.req_accept)
        Button acceptBtn;
        @BindView(R.id.req_decline) Button declineBtn;

        public ReqViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position=getLayoutPosition();
                    Users u= users.get(position);
                    Intent intent = new Intent(itemView.getContext(),ProfileActivity.class);
                    intent.putExtra("User",(Serializable) u);
                    itemView.getContext().startActivity(intent);
                }
            });
           /* acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFriend_Ref
                }
            });*/
        }
        public void SetUser(Users user){
            name.setText(user.getName());
            status.setText(user.getStatus());
            Picasso.get().load(user.getImage()).placeholder(R.drawable.user_icon).into(img);
        }
    }
}
