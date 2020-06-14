package com.example.elcare.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elcare.R;
import com.example.elcare.cards.ChatBox;
import com.example.elcare.cards.ContactCard;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<ContactCard> mContactList;

    private Context context;

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;
        public ImageView mImg;
        public TextView mName;
        public TextView mNumber;
        public TextView mEmergency;


        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            mImg = itemView.findViewById(R.id.contact_img);
            mName = itemView.findViewById(R.id.contact_name);
            mNumber = itemView.findViewById(R.id.contact_num);
            mEmergency = itemView.findViewById(R.id.emergency_contact);
        }
    }

    public ContactAdapter(ArrayList<ContactCard> contactList){
        mContactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
        ContactViewHolder nvh = new ContactViewHolder(v);
        context = parent.getContext();
        return nvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactCard currentItem = mContactList.get(position);

        holder.mImg.setImageResource(currentItem.getmImgResource());
        holder.mName.setText(currentItem.getmName());
        holder.mNumber.setText(currentItem.getmNumber());

        if(currentItem.ismEmergency()){
            holder.mEmergency.setVisibility(View.VISIBLE);
        }else{
            holder.mEmergency.setVisibility(View.GONE);
        }

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add code here to start calling whatever contact
                Log.d("DEBUG", "onClick: start calling");
                Toast.makeText(context, "Calling Contact", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

}
