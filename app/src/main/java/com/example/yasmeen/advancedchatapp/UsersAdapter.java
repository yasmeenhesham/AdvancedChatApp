package com.example.yasmeen.advancedchatapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private static List<Users> users= new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private Users y;

    public UsersAdapter(Context context, List<Users> m) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.users = m;
    }

    @NonNull
    @Override
    public UsersAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.user_single_layout, parent, false);
        UsersViewHolder viewHolder = new UsersViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        y=users.get(position);
        holder.SetUser(y);


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_name) TextView name;
        @BindView(R.id.user_status) TextView status;
        @BindView(R.id.user_image) ImageView img;
        @BindView(R.id.activeImg) ImageView activImg;

        public UsersViewHolder(final View itemView) {
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
                    v.getContext().startActivity(intent);
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
