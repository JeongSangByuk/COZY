package com.example.cozy.SpeakingAPI;

import android.app.Dialog;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cozy.Activity.MainActivity;
import com.example.cozy.Inferface.CallbackListener;
import com.example.cozy.R;
import com.example.cozy.UI.MikeDialog;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;

public class TTSAPI implements TextToSpeechListener {

    public CallbackListener callbackListener;

    public TTSAPI() {
        callbackListener = null;
    }

    @Override
    public void onFinished() {
        Log.d("MainActivity", "TTS API FINISHED.");
    }

    @Override
    public void onError(int code, String message) {
        Log.d("MainActivity", "TTS API ERROR.");
    }


}
