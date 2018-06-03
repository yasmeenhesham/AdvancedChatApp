package com.example.yasmeen.advancedchatapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Message>messages;
    private FirebaseAuth mAuth;
    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        String current_id =mAuth.getCurrentUser().getUid();
        Message message= messages.get(position);
        String from = message.getFrom();
        String type = message.getType();
        if(type.equals("text")) {
            holder.sendImg.setVisibility(View.GONE);
            holder.sendImg2.setVisibility(View.GONE);
            if (current_id.equals(from)) {
                holder.messageText.setVisibility(View.GONE);
                holder.messageImg.setVisibility(View.GONE);
                holder.myMessage.setText(message.getMessage());
                holder.myMessage.setVisibility(View.VISIBLE);
                // holder.messageText.setBackgroundColor(Color.WHITE);
                //holder.messageText.setTextColor(Color.BLACK);
            } else {
                holder.myMessage.setVisibility(View.GONE);
                holder.messageText.setText(message.getMessage());
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageImg.setVisibility(View.VISIBLE);
                // holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                // holder.messageText.setTextColor(Color.WHITE);

            }
        }
        else {
            holder.messageText.setVisibility(View.GONE);
            holder.myMessage.setVisibility(View.GONE);
            if (current_id.equals(from))
            {
                holder.sendImg2.setVisibility(View.VISIBLE);
                holder.sendImg.setVisibility(View.GONE);
                Picasso.get().load(message.getMessage()).placeholder(R.drawable.user_icon).into(holder.sendImg2);
            }
            else
            {
                holder.sendImg.setVisibility(View.VISIBLE);
                holder.sendImg2.setVisibility(View.GONE);
                Picasso.get().load(message.getMessage()).placeholder(R.drawable.user_icon).into(holder.sendImg);
            }
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public ImageView messageImg;
        public  TextView myMessage;
        public  ImageView sendImg;
        public ImageView sendImg2;


        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView)itemView.findViewById(R.id.message_textview);
            messageImg = (ImageView)itemView.findViewById(R.id.messgae_profile_img);
            myMessage = (TextView)itemView.findViewById(R.id.message2_textview);
            sendImg = (ImageView) itemView.findViewById(R.id.message_image);
            sendImg2 = (ImageView)itemView.findViewById(R.id.message_image2);


        }
    }
}
