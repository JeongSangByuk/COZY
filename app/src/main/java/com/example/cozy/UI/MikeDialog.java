package com.example.cozy.UI;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cozy.Activity.MainActivity;
import com.example.cozy.Constant;
import com.example.cozy.R;
import com.example.cozy.SpeakingAPI.STTAPI;
import com.example.cozy.SpeakingAPI.TTSAPI;
import com.example.cozy.SpeakingAPI.TTSAPIInMike;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;

public class MikeDialog extends Dialog implements GestureDetector.OnGestureListener {

    private Context context;
    private MainActivity mainActivity;
    //마이크 애니메이션 생성
    public LottieAnimationView mikeLottieAnimation;
    public ImageButton mikeButton;
    public TextView mikeTextView;
    public TextToSpeechClient ttsClient;
    public STTAPI speechAPI;
    public SpeechRecognizerClient sttClient;
    private GestureDetector gestureScanner;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MikeDialog(@NonNull Context context,MainActivity mainActivity,String ttsString,int radioButtonNumber) {
        super(context);
        this.mainActivity = mainActivity;
        this.context = context;
        gestureScanner = new GestureDetector(this);

        //tts 클라이언트 생성
        ttsClient = new TextToSpeechClient.Builder()
                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     // 음성합성방식
                .setSpeechSpeed(1.0)            // 발음 속도(0.5~4.0)
                .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                .setListener(new TTSAPIInMike(mainActivity,this))
                .build();

        //stt 클라이언트 생성
        String serviceType = SpeechRecognizerClient.SERVICE_TYPE_WEB;
        SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().setServiceType(serviceType);
        speechAPI = new STTAPI(mainActivity,this);
        sttClient = builder.build();
        sttClient.setSpeechRecognizeListener(speechAPI);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        getWindow().setGravity(Gravity.BOTTOM);

        setContentView(R.layout.dialog_mike);

        mikeLottieAnimation = findViewById(R.id.mike_stt_animation);
        mikeTextView = findViewById(R.id.mike_textView);

        //다이얼로그 크기변경
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,350,context.getResources().getDisplayMetrics());
        layoutParams.windowAnimations = R.style.DialogAnimation;
        getWindow().setAttributes(layoutParams);

        //마이크 버튼 이벤트리스너 지정
        mikeButton = (ImageButton) findViewById(R.id.mike_dialog);
        mikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mikeButtonInDialog(view);
            }
        });

        //stt 받으면서 생성 할 경우
        if(ttsString==null) {
            mikeLottieAnimation.setVisibility(LottieAnimationView.VISIBLE);
            mikeLottieAnimation.playAnimation();
            sttClient.startRecording(false);
        }

        //tts 읽으면서 생성할 경우
        else{
            mikeTextView.setText("현재 위치에서 " + radioButtonNumber +"km 반경의 확진자 동선을 알려드릴게요!");
            startTTS(ttsString);
        }

        show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void mikeButtonInDialog(View view){

        //tts 중 일때, mike버튼의 기능은 일시중지!
        if (ttsClient.isPlaying()) {
            ttsClient.stop();
        } else {
            //애니메이션 중일 때, 즉 stt중에!

            if (mikeLottieAnimation.isAnimating()) {
                speechAPI.isCancled = true;
                sttClient.stopRecording();
                mikeTextView.setText("안녕하세요!\nCOZY 에게 말해보세요!");
                mikeButton.setImageResource(R.drawable.main_mike1);
                mikeLottieAnimation.setVisibility(LottieAnimationView.INVISIBLE);
                mikeLottieAnimation.pauseAnimation();
            } else {
                mikeTextView.setText("듣고 있어요!");
                mikeButton.setImageResource(R.drawable.main_mike_in_sst);
                mikeLottieAnimation.setVisibility(LottieAnimationView.VISIBLE);
                mikeLottieAnimation.playAnimation();

                sttClient.startRecording(false);
            }
        }
    }

    //TTS 시작할때 메소드
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startTTS(String ttsString) {

        //stopRecording = stop 하기 이전까지의 검색결과 출력, cancelRecording 아예 삭제
        sttClient.cancelRecording();

        mikeButton.setImageResource(R.drawable.stop);
        mikeLottieAnimation.setAnimation("test8.json");
        mikeLottieAnimation.setVisibility(LottieAnimationView.VISIBLE);
        mikeLottieAnimation.playAnimation();

        ttsClient.play(ttsString);
    }

    public void loadingInMike(){
        Log.d("loadingStart","loadingStart");
        mikeButton.setImageResource(R.drawable.main_mike_in_sstloading);
        mikeLottieAnimation.setAnimation("loading.json");
        mikeLottieAnimation.setVisibility(LottieAnimationView.VISIBLE);
        mikeLottieAnimation.playAnimation();
    }

    //외부 터치시 종료
    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);

        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
            sttClient.cancelRecording();
            ttsClient.stop();
            this.dismiss();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {

        sttClient.cancelRecording();
        ttsClient.stop();
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return gestureScanner.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {

       if (motionEvent1.getY() - motionEvent.getY() > Constant.SWIPE_MIN_DISTANCE && Math.abs(velocityY) > Constant.SWIPE_THRESHOLD_VELOCITY) {
           sttClient.cancelRecording();
           ttsClient.stop();
           dismiss();
        }
        return true;
    }
}
