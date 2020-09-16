package com.example.cozy;

import android.os.Message;
import android.os.Handler;

public class IntroThread extends Thread {

    private Handler handler;

    public IntroThread(Handler handler) {
        this.handler = handler;   // Thread.sleep() 메소드가 종료했을 때 메세지 전달받아야 함
    }


    // 다른 activity 또는 class에서 thread 객체를 생성해 start() 메소드를 호출했을 때 실행
    @Override
    public void run() {
        //super.run();

        Message message = new Message();

        try {
            Thread.sleep(1000);   // 1초
            message.what = 1;
            handler.sendEmptyMessage(message.what);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
