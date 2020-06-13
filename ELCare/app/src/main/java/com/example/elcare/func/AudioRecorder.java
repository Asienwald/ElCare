package com.example.elcare.func;

import android.Manifest;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

public class AudioRecorder {

    private final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private MediaPlayer player;
    private MediaRecorder recorder;

    public void startRecording(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    }

}
