package com.example.myeyes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myeyes.fragment.ComparisionMovingLineFragment;
import com.example.myeyes.fragment.CoronaInformationFragment;
import com.example.myeyes.fragment.IntroFragment;
import com.example.myeyes.fragment.MovingLineFragment;
import com.example.myeyes.fragment.SetUpFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import java.security.MessageDigest;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Context mContext;
    private SpeechRecognizerClient sttClient;
    public TextToSpeechClient ttsClient;
    private static final int REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE = 0;
    private boolean isPermissionGranted = false;
    private STTAPI speechAPI;

    public MainActivity(){ }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //권한을 확인하는 부분, 권한 중 하나라도 퍼미션 거부되어있는 경우우
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE);
        } else {
            //모든 권한이 퍼미션되어있는 경우 실행.
            startUsingSpeechAPI();
        }

        //tts 클라이언트 생성
        ttsClient = new TextToSpeechClient.Builder()
                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_2)     // 음성합성방식
                .setSpeechSpeed(1.0)            // 발음 속도(0.5~4.0)
                .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                .setListener(new TTSAPI())
                .build();

        //init 시 intro fragment를 삽입
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container,new IntroFragment());
        fragmentTransaction.commit();

        getHashKey(this);
    }

    public void speakButtonText(Button button){
        String buttonString = button.getText().toString().replace("\n"," ");
        ttsClient.play(buttonString);
    }

    public void coronaInformationButton (View view){
        //권한이 충족됐을때만 이벤트 실행
        if (!isPermissionGranted) {
            checkPermissionGranted();
            return;
        }

        Button button = findViewById(R.id.button1);
        speakButtonText(button);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container,new CoronaInformationFragment());
        fragmentTransaction.commit();
    }

    public void movingLineButton(View view){
        //권한이 충족됐을때만 이벤트 실행
        if (!isPermissionGranted) {
            checkPermissionGranted();
            return;
        }

        Button button = findViewById(R.id.button2);
        speakButtonText(button);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container,new MovingLineFragment());
        fragmentTransaction.commit();
    }

    public void comparisionMovingLineButton(View view){
        //권한이 충족됐을때만 이벤트 실행
        if (!isPermissionGranted) {
            checkPermissionGranted();
            return;
        }

        Button button = findViewById(R.id.button3);
        speakButtonText(button);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container,new ComparisionMovingLineFragment());
        fragmentTransaction.commit();
    }

    public void setUpButton(View view){
        //권한이 충족됐을때만 이벤트 실행
        if (!isPermissionGranted) {
            checkPermissionGranted();
            return;
        }

        Button button = findViewById(R.id.button4);
        speakButtonText(button);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container,new SetUpFragment());
        fragmentTransaction.commit();

    }

    public void mikeButton(View view) {

        //권한이 충족됐을때만 이벤트 실행
        if (!isPermissionGranted) {
            checkPermissionGranted();
            return;
        }
        speechAPI = new STTAPI();

        //ttsClient.play("듣고 있어요."); 띠링? 같은 소리가 들리면 좋겟다!

        Toast.makeText(this, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        sttClient.setSpeechRecognizeListener(speechAPI);
        sttClient.startRecording( false);

    }

    public void startUsingSpeechAPI() {
        String serviceType = SpeechRecognizerClient.SERVICE_TYPE_WEB;
        //sdk 초기화
        SpeechRecognizerManager.getInstance().initializeLibrary(this);
        TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());
        //퍼미션 flag true
        isPermissionGranted = true;

        //stt 클라이언트 생성
        SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().setServiceType(serviceType);
        sttClient = builder.build();
    }

    public void checkPermissionGranted() {

        //권한을 확인하는 부분, 권한 중 하나라도 퍼미션 거부되어있는 경우
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            //사용자가 맨 처음에 거절을 눌러서 하나라도 퍼미션이 거부된 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE);
            } else {
                // 사용자가 거부하면서 다시 묻지 않기를 클릭 -> 권한이 없다고 사용자에게 직접 알림.
                Toast.makeText(this, "권한 Permission이 거부됐습니다. 어플을 사용하시려면 설정(앱 정보)에서 Permission을 허용해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            startUsingSpeechAPI();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        // API를 더이상 사용하지 않을 때 finalizeLibrary()를 호출한다.
        SpeechRecognizerManager.getInstance().finalizeLibrary();
        TextToSpeechManager.getInstance().finalizeLibrary();
    }

    //퍼미션 체크 후 콜백 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE:

                //모든 권한에 동의한경우 start 메소드 실행
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 승인에 동의했습니다.", Toast.LENGTH_SHORT).show();
                    startUsingSpeechAPI();
                }
                //동의하지 않은 경우
                else {
                    Toast.makeText(this, "권한 승인에 거절하셨습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    //해쉬키 구하는 메소드
    @Nullable
    public static String getHashKey(Context context) {
        final String TAG = "KeyHash";
        String keyHash = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = new String(Base64.encode(md.digest(), 0));
                Log.d(TAG, keyHash);
            }

        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
        if (keyHash != null) {
            return keyHash;

        } else {
            return null;
        }
    }

}