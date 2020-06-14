package com.example.elcare;

import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.elcare.fragments.CallFragment;
import com.example.elcare.fragments.SosFragment;
import com.google.common.base.Function;

import java.util.HashMap;
import java.util.Map;

// this class will handle all actions done by chatbot
public class Action{

    public void action(String code, FragmentTransaction transaction){
        Log.d("DEBUG", "action: " + code);
        switch(code){
            case "###EMERGENCY_DETECTED":
                goToSos(transaction);
                break;
            case "###REDIRECT_CALL":
                goToCall(transaction);
                break;
        }
    }

    private void goToSos(FragmentTransaction transaction){
        // FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.home_fragment, SosFragment.newInstance()).commit();
    }

    private void goToCall(FragmentTransaction transaction){
        // FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.home_fragment, CallFragment.newInstance()).commit();
    }

}
