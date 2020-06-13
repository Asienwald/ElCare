package com.example.elcare.cards;

import com.example.elcare.R;

public class ContactCard {

    private int mImgResource;
    private String mName;
    private String mNumber;
    private boolean mEmergency;

    public ContactCard(int imgRes, String name, String number, boolean emergency){
        mImgResource = imgRes;
        mName = name;
        mNumber = number;
        mEmergency = emergency;
    }

    public int getmImgResource() {
        return mImgResource;
    }

    public String getmName() {
        return mName;
    }

    public String getmNumber() {
        return mNumber;
    }

    public boolean ismEmergency() {
        return mEmergency;
    }
}
