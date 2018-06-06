package com.example.yasmeen.advancedchatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReqAdapter  extends RecyclerView.Adapter<ReqAdapter.ReqViewHolder>{
    private static List<Users> users= new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private Users  y;
    private FirebaseAuth mAuth;
    private DatabaseReference mReq_Ref ;
    private DatabaseReference mDatabaseRefRoot;
    private String current_user ;
    private List<String>names = new ArrayList<>();


    public ReqAdapter(Context mContext, List<Users>m , List<String>names) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.users = m;
        this.names= names;
        mAuth = FirebaseAuth.getInstance();
        mReq_Ref = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        current_user= mAuth.getCurrentUser().getUid();
        mReq_Ref = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mDatabaseRefRoot = FirebaseDatabase.getInstance().getReference();

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
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getLayoutPosition();
                    Users u = users.get(position);
                    final String currentDate = DateFormat.getDateInstance().format(new Date());
                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/"+current_user+"/"+y.getUid()+"/date",currentDate);
                    friendsMap.put("Friends/"+y.getUid()+"/"+current_user+"/date",currentDate);
                    friendsMap.put("Friend_Req/"+current_user+"/"+y.getUid(), null);
                    friendsMap.put("Friend_Req/"+y.getUid()+"/"+current_user, null);
                    mDatabaseRefRoot.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null)
                            {
                                users.remove(position);
                                names.remove(position);

                                UpdateBakingService.startBakingService(mContext,names);
                                notifyDataSetChanged();
                                Toast.makeText(itemView.getContext(), R.string.added_success, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
            declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getLayoutPosition();
                    Users u = users.get(position);

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friend_Req/"+current_user+"/"+y.getUid(), null);
                    friendsMap.put("Friend_Req/"+y.getUid()+"/"+current_user, null);
                    mDatabaseRefRoot.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null)
                            {
                                users.remove(position);
                                names.remove(position);
                                notifyDataSetChanged();
                                UpdateBakingService.startBakingService(mContext,names);
                                Toast.makeText(itemView.getContext(), R.string.decline_success, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

        }
        public void SetUser(Users user){
            name.setText(user.getName());
            status.setText(user.getStatus());
            Picasso.get().load(user.getImage()).placeholder(R.drawable.user_icon).into(img);
        }
    }
}
