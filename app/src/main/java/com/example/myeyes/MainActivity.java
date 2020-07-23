package com.example.myeyes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity{

    private Context mContext;
    private SpeechRecognizerClient sttClient;
    private static final int REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE = 0;
    private boolean isPermissionGranted = false;
    private STTAPI speechAPI;

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

        //getHashKey(this); 해서키보는 메소드
    }

    public void mikeButton(View view) {

        //권한이 충족됐을때만 이벤트 실행
        if (!isPermissionGranted) {
            checkPermissionGranted();
            return;
        }
        speechAPI = new STTAPI();

        sttClient.setSpeechRecognizeListener(speechAPI);
        sttClient.startRecording( false);

        Toast.makeText(this, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
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