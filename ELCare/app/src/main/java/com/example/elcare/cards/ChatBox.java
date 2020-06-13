package com.example.elcare.cards;

import android.graphics.Color;
import android.view.Gravity;

public class ChatBox {

//    private String mCardColour;
//    private String mTextColour;
    private String mText;
//    private Gravity mGravity;
    private boolean mIsJolene;

    public ChatBox(String text, boolean isJolene){
//        mCardColour = cardColour;
//        mTextColour = textColour;
        mText = text;
//        mGravity = gravity;
        mIsJolene = isJolene;

    }

//    public String getCardColour(){return mCardColour;}
//    public String getTextColour(){return mTextColour;}
    public String getText(){return mText;}
//    public Gravity getGravity(){return mGravity;}
    public boolean getIsJolene(){return mIsJolene;}
}
