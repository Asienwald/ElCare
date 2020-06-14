package com.example.elcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.elcare.fragments.MonitorFragment;
import com.example.elcare.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    public static boolean tempMonitor = true;
    public static boolean motionMonitor = true;
    public static boolean soundMonitor = true;


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

}