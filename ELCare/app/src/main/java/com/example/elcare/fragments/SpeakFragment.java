package com.example.elcare.fragments;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elcare.R;
import com.example.elcare.adapters.ChatAdapter;
import com.example.elcare.cards.ChatBox;
import com.example.elcare.itemdecoration.VerticalSpaceItemDecoration;

import java.util.ArrayList;

public class SpeakFragment extends Fragment {

    public static SpeakFragment newInstance() {
        return new SpeakFragment();
    }

    private RecyclerView mRv;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<ChatBox> chatList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speak_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.home_fragment, MainFragment.newInstance()).commit();
            }
        });

        mRv = view.findViewById(R.id.chat_rv);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new ChatAdapter(chatList);

        // item deco to add space between views
        VerticalSpaceItemDecoration dividerItemDecoration = new VerticalSpaceItemDecoration(100);

        mRv.setLayoutManager(mLayoutManager);;
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(dividerItemDecoration);


        view.findViewById(R.id.mic_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg("yes hello");
            }
        });
        view.findViewById(R.id.call_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.home_fragment, CallFragment.newInstance()).commit();
            }
        });
    }

    private void addChat(boolean byJolene, String msg){
        chatList.add(new ChatBox(msg, byJolene));
        mAdapter.notifyDataSetChanged();
    }

    private void sendMsg(String msg){
        addChat(false, msg);
    }
}