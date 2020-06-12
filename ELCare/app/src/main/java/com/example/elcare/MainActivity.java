package com.example.elcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.elcare.ui.main.MonitorFragment;
import com.example.elcare.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {


    public static MainActivity instance;
    // public FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        instance = this;
        if (savedInstanceState == null) {
            MainFragment mainFragment = new MainFragment();
            MonitorFragment monitorFragment = new MonitorFragment();


            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // fragmentTransaction.replace(R.id.home_fragment,home_page_fragment).commit();
            // fragmentTransaction.add(R.id.home_fragment, news_page_fragment).commit();
            fragmentTransaction.add(R.id.home_fragment, mainFragment).commit();
        }

    }
//
//    public static void fragmentTransaction(Fragment fragment){
//        Log.d("debug","fragment");
//        FragmentManager fragmentManager = instance.getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.home_fragment, MonitorFragment.newInstance()).commit();
//    }
//
//    public void replaceFragments(Class fragmentClass) {
//        Fragment fragment = null;
//        try {
//            fragment = (Fragment) fragmentClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.home_fragment, fragment)
//                .commit();
//    }

}