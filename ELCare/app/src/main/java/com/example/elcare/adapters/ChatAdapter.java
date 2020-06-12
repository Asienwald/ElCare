package com.example.elcare.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elcare.R;
import com.example.elcare.cards.ChatBox;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private ArrayList<ChatBox> mChatList;

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        public CardView mCard;
        public TextView mText;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mCard = itemView.findViewById(R.id.text_card);
            mText = itemView.findViewById(R.id.text);
        }
    }

    public ChatAdapter(ArrayList<ChatBox> chatList){
        mChatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_box, parent, false);
        ChatViewHolder nvh = new ChatViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatBox currentItem = mChatList.get(position);

//        holder.mCard.setBackgroundColor(Color.parseColor(currentItem.getCardColour()));
//        holder.mText.setTextColor(Color.parseColor(currentItem.getTextColour()));
        if(currentItem.getIsJolene()){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 0f;
            params.gravity = Gravity.START;
            holder.mCard.setLayoutParams(params);
            holder.mCard.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.mText.setTextColor(Color.parseColor("#000000"));
        }else{
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 0f;
            params.gravity = Gravity.END;
            holder.mCard.setLayoutParams(params);
            holder.mCard.setBackgroundColor(Color.parseColor("#50DAFF"));
            holder.mText.setTextColor(Color.parseColor("#ffffff"));
        }
        holder.mText.setText(currentItem.getText());

    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

}
