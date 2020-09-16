package com.example.cozy.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cozy.Activity.MainActivity;
import com.example.cozy.Adapter.InformationViewPager2Adapter;
import com.example.cozy.Adapter.InformationViewPagerAdapter;
import com.example.cozy.Fragment.MapFragment;
import com.example.cozy.Inferface.CallbackListener;
import com.example.cozy.R;
import com.pm10.library.CircleIndicator;

import me.relex.circleindicator.CircleIndicator2;
import me.relex.circleindicator.CircleIndicator3;

public class InformationDialog {

    private ViewPager2 informationViewPager;
    private InformationViewPager2Adapter informationViewPagerAdapter;

    private Context context;
    public Dialog dialog;
    public MainActivity mainActivity;
    private String guideString;

    public InformationDialog(Context context) {
        this.context = context;
        mainActivity = (MainActivity)context;

    }

    public void makeInformationDialog() {

        dialog = new Dialog(context);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_information);

        informationViewPager = dialog.findViewById(R.id.informationViewPager);

        dialog.show();




        informationViewPager = dialog.findViewById(R.id.informationViewPager);
        informationViewPagerAdapter = new InformationViewPager2Adapter(mainActivity);

        informationViewPager.setAdapter(informationViewPagerAdapter);
        informationViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        informationViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        guideString = "안녕하세요. 저는 코지 입니다. 지금부터 코지 사용법 음성 안내를 시작할게요.";
                        break;

                    case 1:
                        guideString = "코지는 매일 코로나 정보를 업데이트 해요. 마이크 버튼을 눌러서, 오늘 확진자 수 알려줘. 서울시 강남구 확진자 알려줘. 와 같이" +
                                " 코지한테 질문해보세요.";
                        break;

                    case 2:
                        guideString = "코지는 현재 위치 주변 확진자 동선 정보도 알려줘요. 주변 확진자 동선 알려줘. 와 같이 질문해보세요.";
                        break;

                    case 3:
                        guideString = "코로나 정보 메뉴에서는, 코로나 현황 정보를 한눈에 알 수 있어요.";
                        break;

                    case 4:
                        guideString = "확진자 동선 메뉴에서, 내 주소 추가하기,를 눌러서, 자신만의 주소를 등록해보세요. 편리하게" +
                                " 확진자 동선 정보를 받을 수 있어요.";
                        break;

                    case 5:
                        guideString = "동선 비교 메뉴에서는, 현재 위치로부터 일정 거리만큼의 확진자 동선 정보를 알 수 있어요." +
                                " 오늘도, 코지, 하세요.";
                        break;
                }

                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.ttsClient.stop();
                    }


                });

                Thread threadState = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true){
                            Thread.State state = thread.getState();

                            if(state == Thread.State.NEW)
                                thread.start();

                            if(state == Thread.State.TERMINATED){
                                Log.d("sdsd","sd");
                                mainActivity.ttsClient.play(guideString);
                                break;
                            }

                            try {
                                Log.d("sdsd","sd111");
                                Thread.sleep(1000);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });

                threadState.start();

                super.onPageSelected(position);

            }
        });

        CircleIndicator3 circleIndicator3 = (CircleIndicator3) dialog.findViewById(R.id.indicatorInformationDialog);
        circleIndicator3.setViewPager(informationViewPager);


      /*  CircleIndicator circleIndicator = (CircleIndicator) dialog.findViewById(R.id.indicatorInformationDialog);
        circleIndicator.setupWithViewPager(informationViewPager);*/
    }

}
