package com.example.elcare.fragments;

import android.os.AsyncTask;
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
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.DeleteSessionOptions;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeIntent;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import com.ibm.watson.assistant.v2.model.SessionResponse;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.tone_analyzer.v3.model.ToneScore;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatFragment extends Fragment {

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    private RecyclerView mRv;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<ChatBox> chatList = new ArrayList<>();

    private Assistant assService;
    //private SessionResponse session;
    private String sessionId;
    private final String assID = "3396bbc9-b863-416c-ae9d-0881d31ab73d";

    private boolean askingInput = false;
    private String inputAction = null;


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

    private class ConverseTask extends AsyncTask<String, Void, List>{

        @Override
        protected List<RuntimeResponseGeneric> doInBackground(String... strings) {

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

            List<RuntimeIntent> intents = response.getOutput().getIntents();

//            DeleteSessionOptions delOp = new DeleteSessionOptions.Builder(assID, sessionId).build();
//            assService.deleteSession(delOp).execute();
            List list = Arrays.asList(responseGeneric, intents);
            return list;
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);

            try {
                List<RuntimeResponseGeneric> responseGeneric = (List<RuntimeResponseGeneric>) list.get(0);
                List<RuntimeIntent> intents = (List<RuntimeIntent>) list.get(1);
                String intent = intents.get(0).intent();

                Log.d("INTENT", "onPostExecute: " + intent);

                if(responseGeneric.size() > 0) {
                    String[] responseList = responseGeneric.get(0).text().split("\n");
                    for (String res : responseList){
                        if(res.contains("###")){
                            switch(res){
                                case "###CONTACT_EMERGENCY":
                                    askingInput = true;
                                    inputAction = "EMERGENCY";
                                    break;
                            }
                        }else{
                            addChat(true, res);
                        }
                    }
                    // do simple input logic here
                    if(askingInput){
                        if(intent.equals("yes") && inputAction.equals("EMERGENCY")){
                            raiseEmergency();
                        }
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
        return inflater.inflate(R.layout.chat_fragment, container, false);
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
        EditText et = view.findViewById(R.id.et_msg);
        et.setFilters(new InputFilter[]{});

        mRv = view.findViewById(R.id.chat_rv);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new ChatAdapter(chatList);

        // item deco to add space between views
        VerticalSpaceItemDecoration dividerItemDecoration = new VerticalSpaceItemDecoration(100);

        mRv.setLayoutManager(mLayoutManager);;
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(dividerItemDecoration);


        view.findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = view.findViewById(R.id.et_msg);
                String msg = et.getText().toString();
                sendMsg(msg);
            }
        });
        view.findViewById(R.id.call_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.home_fragment, CallFragment.newInstance()).commit();
            }
        });

        StartSessionTask task = new StartSessionTask();
        task.execute();

        view.findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = view.findViewById(R.id.et_msg);
                String msg = et.getText().toString();

                sendMsg(msg);
                et.setText("");

                ConverseTask task = new ConverseTask();
                task.execute(msg);
            }
        });
    }

    private void raiseEmergency(){
        getFragmentManager().beginTransaction().replace(R.id.home_fragment, SosFragment.newInstance()).commit();
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