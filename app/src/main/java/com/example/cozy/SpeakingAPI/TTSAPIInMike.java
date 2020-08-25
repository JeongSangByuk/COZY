package com.example.cozy.SpeakingAPI;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cozy.Activity.MainActivity;
import com.example.cozy.R;
import com.example.cozy.UI.MikeDialog;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;

public class TTSAPIInMike implements TextToSpeechListener {

    private MainActivity mainActivity;
    private MikeDialog mikeDialog;

    public TTSAPIInMike(MainActivity mainActivity, MikeDialog mikeDialog) {
        this.mainActivity = mainActivity;
        this.mikeDialog = mikeDialog;
    }

    @Override
    public void onFinished() {
        Log.d("MainActivity", "TTS API IN DIALOG FINISHED.");

        mainActivity.runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                mikeDialog.mikeButton.setImageResource(R.drawable.main_mike1);
                mikeDialog.mikeLottieAnimation.setVisibility(LottieAnimationView.INVISIBLE);
                mikeDialog.mikeLottieAnimation.pauseAnimation();
                mikeDialog.mikeTextView.setText("안녕하세요!\nCOZY 에게 말해보세요!");
            }
        });
    }

    @Override
    public void onError(int code, String message) {
        Log.d("MainActivity", "TTS API ERROR.");
        mainActivity.runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                mikeDialog.mikeButton.setImageResource(R.drawable.main_mike1);
                mikeDialog.mikeLottieAnimation.setVisibility(LottieAnimationView.INVISIBLE);
                mikeDialog.mikeLottieAnimation.pauseAnimation();
                mikeDialog.mikeTextView.setText("안녕하세요!\nCOZY 에게 말해보세요!");
            }
        });
    }


}
