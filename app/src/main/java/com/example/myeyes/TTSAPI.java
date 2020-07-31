package com.example.myeyes;

import android.util.Log;

import com.kakao.sdk.newtoneapi.TextToSpeechListener;

public class TTSAPI implements TextToSpeechListener {
    @Override
    public void onFinished() {
        Log.d("MainActivity", "TTS API FINISHED.");
    }

    @Override
    public void onError(int code, String message) {
        Log.d("MainActivity", "TTS API ERROR.");
    }
}
