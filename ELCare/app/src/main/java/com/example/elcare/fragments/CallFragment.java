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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elcare.R;
import com.example.elcare.adapters.ChatAdapter;
import com.example.elcare.adapters.ContactAdapter;
import com.example.elcare.cards.ChatBox;
import com.example.elcare.cards.ContactCard;
import com.example.elcare.itemdecoration.VerticalSpaceItemDecoration;

import java.util.ArrayList;

public class CallFragment extends Fragment {

    public static CallFragment newInstance() {
        return new CallFragment();
    }

    private RecyclerView mRv;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<ContactCard> contactList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.call_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "clicked");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                transaction.replace(R.id.home_fragment, MainFragment.newInstance()).commit();
            }
        });

        mRv = view.findViewById(R.id.contact_rv);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new ContactAdapter(contactList);


        mRv.setLayoutManager(mLayoutManager);;
        mRv.setAdapter(mAdapter);

        addContact(R.drawable.default_user, "Son", "65744444", true);
    }


    private void addContact(int img, String name, String number, boolean emergency){
        contactList.add(new ContactCard(img, name, number, emergency));
    }
}