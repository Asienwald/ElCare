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
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.DeleteSessionOptions;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import com.ibm.watson.assistant.v2.model.SessionResponse;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionAlternative;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.tone_analyzer.v3.model.ToneScore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

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

    private Assistant assService;
    private SessionResponse session;
    private String sessionId;
    private final String assID = "3396bbc9-b863-416c-ae9d-0881d31ab73d";

    private class AskWatsonTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... Strings) {
            String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
            recordFilePath = "tmp.ogg";

            Authenticator authenticator = new IamAuthenticator("Sayq6MpAznHMEdw7RxYuzyLKuCCReo9rhp42WpjHVRsZ");
            SpeechToText service = new SpeechToText(authenticator);
            service.setServiceUrl("https://api.jp-tok.speech-to-text.watson.cloud.ibm.com/instances/ffcdeada-0c2e-4911-b2e7-8a81b7ba7c89");

            File audio = new File(recordPath + "/" + recordFilePath);
            try {
                RecognizeOptions options = new RecognizeOptions.Builder().audio(audio).contentType("audio/ogg").build();

                SpeechRecognitionResults transcript = service.recognize(options).execute().getResult();
//            Log.d("DEBUG", "speechToText: " + transcript.toString());

                String saidText = transcript.getResults().get(0).getAlternatives().get(0).getTranscript();

                // lastSaidText = saidText;
                return saidText;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "Something went wrong..";
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("DEBUG", "You said: " + s);

            sendMsg(s);
             // analyseTone(s);

            AnalyseToneTask task2 = new AnalyseToneTask();
            task2.execute(s);

            ConverseTask task3 = new ConverseTask();
            task3.execute(s);
        }
    }

    private class AnalyseToneTask extends AsyncTask<String, Void, List>{


        @Override
        protected List doInBackground(String... strings) {
            Authenticator authenticator = new IamAuthenticator("Q05DrxA4q6_84PVwbdDNI8sBrT1oTqW6LfjowdzN96xZ");
            ToneAnalyzer service = new ToneAnalyzer("2020-06-13", authenticator);

            ToneOptions toneOptions = new ToneOptions.Builder()
                    .text(strings[0])
                    .build();
            ToneAnalysis toneAnalysis = service.tone(toneOptions).execute().getResult();
            Log.d("DEBUG", "analyseTone: " + toneAnalysis);
            List<ToneScore> tones = toneAnalysis.getDocumentTone().getTones();

            // display each tone accordingly
            return tones;
        }

        @Override
        protected void onPostExecute(List tones) {
            super.onPostExecute(tones);
            Log.d("DEBUG", "your tones: " + tones);
        }
    }

    private class ConverseTask extends AsyncTask<String, Void, List<RuntimeResponseGeneric>>{

        @Override
        protected List<RuntimeResponseGeneric> doInBackground(String... strings) {
//            Authenticator auth = new IamAuthenticator("5iKnzcjGqkjGxXJd8BSPDJ0-bS0wvZ5Q7QmC2d7LSOKn");
//            com.ibm.watson.assistant.v2.Assistant service = new Assistant("2020-06-13", auth);
//            final String assID = "3396bbc9-b863-416c-ae9d-0881d31ab73d";
//
//            // create session
//            CreateSessionOptions op = new CreateSessionOptions.Builder(assID).build();
//            SessionResponse session = service.createSession(op).execute().getResult();
//            String sessionId = session.getSessionId();

            com.ibm.watson.assistant.v2.model.MessageInput input = new MessageInput.Builder()
                    .text(strings[0])
                    .build();
            // Log.d("DEBUG", "doInBackground: " + strings[0]);
            com.ibm.watson.assistant.v2.model.MessageOptions options = new MessageOptions.Builder()
                    .assistantId(assID)
                    .sessionId(sessionId)
                    .input(input)
                    .build();
            MessageResponse response = assService.message(options).execute().getResult();
            Log.d("DEBUG", "doInBackground: " + response);

            // Print the output from dialog, if any. Assumes a single text response.
            List<RuntimeResponseGeneric> responseGeneric = response.getOutput().getGeneric();

//            DeleteSessionOptions delOp = new DeleteSessionOptions.Builder(assID, sessionId).build();
//            assService.deleteSession(delOp).execute();
            return responseGeneric;
        }

        @Override
        protected void onPostExecute(List<RuntimeResponseGeneric> responseGeneric) {
            super.onPostExecute(responseGeneric);

            try {
                if(responseGeneric.size() > 0) {
                    String[] responseList = responseGeneric.get(0).text().split("\n");
                    for (String res : responseList){
                        addChat(true, res);
                    }
                }
            }catch(Exception e){
                addChat(true, "Sorry, I don't understand.");
            }
        }
    }

    private class StartSessionTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // init first text from jolene
            ConverseTask task = new ConverseTask();
            task.execute("hello");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Authenticator auth = new IamAuthenticator("5iKnzcjGqkjGxXJd8BSPDJ0-bS0wvZ5Q7QmC2d7LSOKn");
            assService = new Assistant("2020-06-13", auth);
            CreateSessionOptions op = new CreateSessionOptions.Builder(assID).build();
            SessionResponse session = assService.createSession(op).execute().getResult();
            sessionId = session.getSessionId();
            return null;
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

                Thread deleteSess = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DeleteSessionOptions delOp = new DeleteSessionOptions.Builder(assID, sessionId).build();
                        assService.deleteSession(delOp).execute();
                    }
                });
                deleteSess.start();

                getFragmentManager().beginTransaction().replace(R.id.home_fragment, MainFragment.newInstance()).commit();
            }
        });

        mRv = view.findViewById(R.id.chat_rv);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new ChatAdapter(chatList);

        // item deco to add space between views
        VerticalSpaceItemDecoration dividerItemDecoration = new VerticalSpaceItemDecoration(100);

        mRv.setLayoutManager(mLayoutManager);
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

//        Thread startSession = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Authenticator auth = new IamAuthenticator("5iKnzcjGqkjGxXJd8BSPDJ0-bS0wvZ5Q7QmC2d7LSOKn");
//                assService = new Assistant("2020-06-13", auth);
//                CreateSessionOptions op = new CreateSessionOptions.Builder(assID).build();
//                SessionResponse session = assService.createSession(op).execute().getResult();
//
//                // init first text from jolene
//                ConverseTask task = new ConverseTask();
//                task.execute("hello");
//            }
//        });
//        startSession.start();
        StartSessionTask task = new StartSessionTask();
        task.execute();
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

        AskWatsonTask task = new AskWatsonTask();
        task.execute();
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
        mRv.smoothScrollToPosition(chatList.size() - 1);
    }

    private void sendMsg(String msg){
        addChat(false, msg);
    }
}