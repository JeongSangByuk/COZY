package com.example.cozy.SpeakingAPI;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;

import com.example.cozy.Fragment.MapFragment;

import androidx.annotation.RequiresApi;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cozy.Activity.MainActivity;
import com.example.cozy.R;
import com.example.cozy.Server.Post;
import com.example.cozy.UI.MikeDialog;
import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class STTAPI implements SpeechRecognizeListener {
    public String sstString, testString = "";
    private MainActivity mainActivity;
    private MikeDialog mikeDialog;
    public Boolean isCancled=false;

    String[] forwardToServer = new String[8];
    //String[] chatBotMessage = new String[4];

    public STTAPI(MainActivity mainActivity, MikeDialog mikeDialog) {

        this.mainActivity = mainActivity;
        this.mikeDialog = mikeDialog;
    }

    @Override
    public void onReady() {
        Log.d("MainActivity", "모든 준비가 완료 되었습니다.");
        isCancled=false;
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("MainActivity", "말하기 시작 했습니다.");

    }

    @Override
    public void onEndOfSpeech() {
        Log.d("MainActivity", "말하기가 끝났습니다.");
    }

    @Override
    public void onError(final int errorCode, String errorMsg) {
        Log.d("MainActivity", "STT API ERROR.");

        mainActivity.runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                mikeDialog.mikeButton.setImageResource(R.drawable.main_mike1);
                mikeDialog.mikeLottieAnimation.setAnimation("test8.json");
                mikeDialog.mikeLottieAnimation.setVisibility(LottieAnimationView.INVISIBLE);
                mikeDialog.mikeLottieAnimation.pauseAnimation();

                if(!isCancled)
                    mikeDialog.mikeTextView.setText("잘 들리지 않거나 오류가 발생했어요!\nCOZY 에게 말해보세요!");

            }
        });
    }

    @Override
    public void onPartialResult(String partialResult) {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResults(Bundle results) {

        Log.d("!!!!!", "onResults");
        final ArrayList<String> texts = results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);

        sstString = texts.get(0);
        Log.d("MainActivity", "Result texts: " + texts);
        Log.d("MainActivity", "Result!!!!! sstString: " + sstString);

        forwardToServer[0] = "url";
        forwardToServer[1] = "http://ec2-13-209-74-229.ap-northeast-2.compute.amazonaws.com:3000/danbee";
        forwardToServer[2] = "userInput";

        forwardToServer[3] = sstString;
        forwardToServer[4] = "latitude";
        forwardToServer[5] = String.valueOf(MapFragment.currentPosition.latitude);
        forwardToServer[6] = "longitude";
        forwardToServer[7] = String.valueOf(MapFragment.currentPosition.longitude);

        mainActivity.runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                mikeDialog.mikeTextView.setText(sstString);
                mikeDialog.loadingInMike();
                Log.d("test1", "wwwwwwwwww");
            }
        });

        //다른 쓰레드에서 처리.
        Runnable nameOfRunnable = new Runnable()
        {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run()
            {
                connectServer();
            }
        };

        Thread thread = new Thread(nameOfRunnable);
        thread.start();

    }



    @Override
    public void onAudioLevel(float audioLevel) {

    }

    @Override
    public void onFinished() {
        Log.d("MainActivity", "STT API FINISHED.");
    }


    // POST 방식으로 서버랑 연결
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void connectServer() {

        Log.d("!!!!!", "connectPost");

        Post post = new Post();
        post.execute(forwardToServer);

        // json으로 string 값 받아오기
        String jsonString = "";

        try {
            Log.d("!!!!!", "json" + jsonString);

            jsonString = jsonString + post.get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getServerInformation(jsonString);
    }


    // 서버에서 받은 정보
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getServerInformation(String jsonString) {

        try {
            Log.d("!!!!", "getServerInformation");

            Log.d("!!!!!", "jsonString: " + jsonString);

            JSONObject jsonObject = new JSONObject(jsonString);

            try {
                testString = jsonObject.getString("message");
            }catch (Exception e){
                onError(0000,"NoValue");
                Log.d("noValueError","NoValueError");
                return;
            }


            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    mainActivity.runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            Log.d("test3", "wwwwwwwwww");
                            mikeDialog.startTTS(testString);
                        }
                    });

                }
            }, 500);
        } catch (JSONException e) {

            Log.d("!!!!! here", "JSONException");

            e.printStackTrace();
        }
    }


    /*
    // POST 방식으로 챗봇 연결
    private void connectChatBot() {

        Log.d("!!!!!","connectChatBot");

        ChatBot chatBot = new ChatBot();
        chatBot.execute(chatBotMessage);

        // json으로 string 값 받아오기
        String jsonString = "";

        try {
            Log.d("!!!!!","json"+jsonString);

            jsonString= jsonString + chatBot.get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getChatbotInformation(jsonString);
    }


    // 챗봇에서 받은 정보
    private void getChatbotInformation(String jsonString) {
        JSONObject responseSet, result;
        JSONArray jsonArrayResult;
        JSONObject JSONObjectMessage;
        String message;

        try {
            Log.d("!!!!","getChatbotInformation");

            Log.d("!!!!!","jsonString: "+jsonString);

            JSONObject jsonObject = new JSONObject(jsonString);

            responseSet = jsonObject.getJSONObject("responseSet");
            result = responseSet.getJSONObject("result");
            jsonArrayResult = result.getJSONArray("result");
            JSONObjectMessage = jsonArrayResult.getJSONObject(0);

            message = JSONObjectMessage.getString("message");

            Log.d("!!!!!","message:    "+message);


        } catch (JSONException e) {

            Log.d("!!!!! here","JSONException");
            e.printStackTrace();
        }
    }

     */
}
