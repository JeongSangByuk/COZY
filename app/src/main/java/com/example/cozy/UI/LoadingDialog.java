package com.example.cozy.UI;


import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialog {

    public static ProgressDialog loadingDialog = null;

    public LoadingDialog() {

    }

    public void setProgressDialog (Context context) {

        // 로딩 화면
        loadingDialog = new ProgressDialog(context);
        loadingDialog.setMessage("loading...");   // 메세지
        loadingDialog.setCancelable(true);   // 실행 도중 취소 가능 여부
        loadingDialog.getWindow().setLayout(30,10);   // 크기 조절
        loadingDialog.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
        // progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        loadingDialog.show();   // 실행

    }


    /*
    public void endProgressDialog() {
        loadingDialog.dismiss();   // 로딩창 없애기
    }

     */

}
