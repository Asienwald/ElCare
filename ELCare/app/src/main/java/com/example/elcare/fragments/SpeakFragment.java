package com.example.elcare.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elcare.R;
import com.example.elcare.adapters.ChatAdapter;
import com.example.elcare.cards.ChatBox;
import com.example.elcare.itemdecoration.VerticalSpaceItemDecoration;
import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.tone_analyzer.v3.model.ToneOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class SpeakFragment extends Fragment {

    public static SpeakFragment newInstance() {
        return new SpeakFragment();
    }

    private RecyclerView mRv;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<ChatBox> chatList = new ArrayList<>();

    private final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean micBtnPressed;
    private MediaRecorder recorder;
    private String recordFilePath;

    private class AskWatsonTask extends AsyncTask<File, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(File... Files) {

            File audio = Files[0];

            Authenticator authenticator = new IamAuthenticator("Q05DrxA4q6_84PVwbdDNI8sBrT1oTqW6LfjowdzN96xZ");

            String text = "I love the sunny weather today!";
            ToneAnalyzer service = new ToneAnalyzer("2020-06-13", authenticator);
            ToneOptions toneOptions = new ToneOptions.Builder().text(text).build();

            ToneAnalysis tone = service.tone(toneOptions).execute().getResult();
            Log.d("DEBUG", "doInBackground: " + tone);

            return tone.toString();
        }



        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null){
                Log.d("DEBUG", "onPostExecute: " + s);
            }
        }
    }

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

        view.findViewById(R.id.call_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.home_fragment, CallFragment.newInstance()).commit();
            }
        });

        view.findViewById(R.id.mic_btn).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(checkPermission()){
                    micBtnPressed = true;
                    startRecording();

                    ImageView mic = view.findViewById(R.id.mic_btn);
                    mic.setImageResource(R.drawable.rec);
                }
                return true;
            }
        });

//         check if stop holding mic button
        view.findViewById(R.id.mic_btn).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(micBtnPressed){
                        micBtnPressed = false;
                        stopRecording();

                        ImageView mic = view.findViewById(R.id.mic_btn);
                        mic.setImageResource(R.drawable.mic);
                    }
                }
                return true;
            }
        });


        // START CODE FOR AUDIO INPUT
        // get permissions
        int permission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d("DEBUG", "Permission to record denied");
            // do code to request permission again
        }

    }

    private void speechToText(){
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        recordFilePath = "tmp.ogg";

        Authenticator authenticator = new IamAuthenticator("Sayq6MpAznHMEdw7RxYuzyLKuCCReo9rhp42WpjHVRsZ");
        SpeechToText service = new SpeechToText(authenticator);
        service.setServiceUrl("https://api.jp-tok.speech-to-text.watson.cloud.ibm.com/instances/ffcdeada-0c2e-4911-b2e7-8a81b7ba7c89");

        File audio = new File(recordPath + "/" + recordFilePath);
        try {
            RecognizeOptions options = new RecognizeOptions.Builder().audio(audio).contentType("audio/ogg").build();

            SpeechRecognitionResults transcript = service.recognize(options).execute().getResult();
            Log.d("DEBUG", "speechToText: " + transcript.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // convert mp4 file to mp3
    private void convertFile(){

        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        recordFilePath = "tmp.mp4";

        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // everything good
                Log.d("DEBUG", "onSuccess: file converted" + convertedFile.getAbsolutePath());
            }
            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
            }
        };

        AndroidAudioConverter.with(getContext()).setFile(new File(recordPath + "/" + recordFilePath)).setFormat(AudioFormat.MP3)
                .setCallback(callback).convert();
    }

    private void stopRecording(){
        Log.d("DEBUG", "stopRecording: stopped recording");
        recorder.stop();
        recorder.release();
        recorder = null;


        // speechToText();
        // convertFile();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                speechToText();
            }
        });
        thread.start();

    }

    private void startRecording(){
        Log.d("DEBUG", "startRecording: start recording");
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        recordFilePath = "tmp.ogg";


        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.OGG);
        recorder.setOutputFile(recordPath + "/" + recordFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.OPUS);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
    }


    private boolean checkPermission(){
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
            return true;

        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO,
                                                                            Manifest.permission.INTERNET}, REQUEST_RECORD_AUDIO_PERMISSION);
            return false;
        }
    }


    private void addChat(boolean byJolene, String msg){
        chatList.add(new ChatBox(msg, byJolene));
        mAdapter.notifyDataSetChanged();
    }

    private void sendMsg(String msg){
        addChat(false, msg);
    }
}