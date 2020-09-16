package com.example.cozy.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cozy.BackPressCloseHandler;
import com.example.cozy.Constant;
import com.example.cozy.Data.Database;
import com.example.cozy.R;
import com.example.cozy.SpeakingAPI.STTAPI;
import com.example.cozy.SpeakingAPI.TTSAPI;

import com.example.cozy.Fragment.ComparisionMovingLineFragment;
import com.example.cozy.Fragment.CoronaInformationFragment;
import com.example.cozy.Fragment.IntroFragment;
import com.example.cozy.Fragment.MapFragment;
import com.example.cozy.Fragment.MovingLineFragment;

import com.example.cozy.Fragment.ComparisionMovingLineFragment;
import com.example.cozy.Fragment.CoronaInformationFragment;
import com.example.cozy.Fragment.IntroFragment;
import com.example.cozy.Fragment.MapFragment;
import com.example.cozy.Fragment.MovingLineFragment;

import com.example.cozy.UI.MikeDialog;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    public FragmentManager fragmentManager;
    public TextToSpeechClient ttsClient;
    private static final int REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE = 0;
    private boolean isPermissionGranted = false;

    private View view;
    private LayoutInflater layoutInflater;
    private Database adrressDatabase;

    public IntroFragment introFragment;


    private BackPressCloseHandler backPressCloseHandler;

    //중복 터치막기 위해
    private long LastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPressCloseHandler = new BackPressCloseHandler(this);

        IntroActivity introActivity = (IntroActivity) IntroActivity.introActivity;
        introActivity.finish();

        //데이타 베이스
        adrressDatabase = Database.getInstance(MainActivity.this);

        //권한을 확인하는 부분, 권한 중 하나라도 퍼미션 거부되어있는 경우
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE);

        } else {
            //모든 권한이 퍼미션되어있는 경우 실행.
            startUsingSpeechAPI();
        }

        //tts 클라이언트 생성
        ttsClient = new TextToSpeechClient.Builder()
                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     // 음성합성방식
                .setSpeechSpeed(1.0)            // 발음 속도(0.5~4.0)
                .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                .setListener(new TTSAPI())
                .build();

        introFragment = new IntroFragment(this);

        //init 시 intro fragment를 삽입
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, introFragment,"intro");
        fragmentTransaction.commit();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction2 = fm.beginTransaction();
        fragmentTransaction2.add(R.id.current, new MapFragment());
        fragmentTransaction2.commit();
    }


    //뒤로가기 버튼
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            backPressCloseHandler.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    //색을 자연스럽게 변화시키는 메소드
    @SuppressLint("ResourceType")
    public void changeBackgroundColor() {
        LinearLayout mainLayout = findViewById(R.id.main_layout);

        ColorDrawable[] color = {new ColorDrawable(Color.parseColor(getString(R.color.mainBackgroundColor))),
                new ColorDrawable(Color.parseColor(getString(R.color.newbackgroundcolor)))};

        TransitionDrawable mainLayoutTransition = new TransitionDrawable(color);

        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mainLayout.setBackgroundDrawable(mainLayoutTransition);
        } else {
            mainLayout.setBackground(mainLayoutTransition);
        }
        mainLayoutTransition.startTransition(700);
    }

    @SuppressLint("ResourceType")
    public void changeOriginBackgroundColor() {

        LinearLayout mainLayout = findViewById(R.id.main_layout);
        mainLayout.setBackgroundColor(Color.parseColor(getString(R.color.mainBackgroundColor)));
    }


    public void speakButtonText(TextView textView) {

        String buttonString = textView.getText().toString().replace("\n", " ");
        ttsClient.play(buttonString);

    }

    public void coronaInformationButton(TextView textView) {
        //권한이 충족됐을때만 이벤트 실행
        if (!isPermissionGranted) {
            checkPermissionGranted();
            return;
        }

        speakButtonText(textView);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container, new CoronaInformationFragment(),"f");
        fragmentTransaction.commit();

        changeBackgroundColor();
    }

    public void movingLineButton(View view) {
        //권한이 충족됐을때만 이벤트 실행
        if (!isPermissionGranted) {
            checkPermissionGranted();
            return;
        }

        TextView textView = findViewById(R.id.button2);
        speakButtonText(textView);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container, new MovingLineFragment(),"f");
        fragmentTransaction.commit();

        changeBackgroundColor();
    }

    public void comparisionMovingLineButton(View view) {
        //권한이 충족됐을때만 이벤트 실행
        if (!isPermissionGranted) {
            checkPermissionGranted();
            return;
        }

        TextView textView = findViewById(R.id.button3);
        speakButtonText(textView);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container, new ComparisionMovingLineFragment(this),"f");
        fragmentTransaction.commit();

        changeBackgroundColor();
    }

    //프래그먼트에서 뒤로가기 버튼 메소드
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void goToBackStage(View view) {
        onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void mikeButton(View view) {

        //중복 터치 막기
        if (SystemClock.elapsedRealtime() - LastClickTime < Constant.MINIMUM_CLICK_INTERVAL)
            return;
        LastClickTime = SystemClock.elapsedRealtime();

        //권한이 충족됐을때만 이벤트 실행
        if (!isPermissionGranted) {
            checkPermissionGranted();
            return;
        }

        //다이얼로그 띄우면서, 시작
        MikeDialog mikeDialog = new MikeDialog(this,this,null,0);


        //뷰페이저 멈추기!
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(fragment.getTag().equals("intro")){
            introFragment.timer.cancel();

            mikeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    introFragment.restartViewPager();
                }
            });
        }

    }

    public void startUsingSpeechAPI() {

        //sdk 초기화
        SpeechRecognizerManager.getInstance().initializeLibrary(this);
        TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());
        //퍼미션 flag true
        isPermissionGranted = true;
    }

    public void checkPermissionGranted() {

        //권한을 확인하는 부분, 권한 중 하나라도 퍼미션 거부되어있는 경우
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //사용자가 맨 처음에 거절을 눌러서 하나라도 퍼미션이 거부된 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE);

            }
            else {
                // 사용자가 거부하면서 다시 묻지 않기를 클릭 -> 권한이 없다고 사용자에게 직접 알림.
                Toast.makeText(this, "권한이 거부됐습니다. 어플을 사용하시려면 설정에서 허용해주세요.", Toast.LENGTH_SHORT).show();
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

        Log.d("!!!!!@@@",String.valueOf(grantResults.length));

        switch (requestCode) {
            case REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE:

                //모든 권한에 동의한경우 start 메소드 실행
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
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

