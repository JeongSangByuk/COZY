package com.example.cozy.UI;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.ListView;

import com.example.cozy.Data.AdrressData;
import com.example.cozy.Adapter.MovingLineAdapter;
import com.example.cozy.R;

import java.util.ArrayList;

public class MovingLineDialog {

    private Context context;
    private ArrayList<AdrressData> movingLineAdrressList;
    public Dialog dialog;

    public MovingLineDialog(Context context, ArrayList<AdrressData> movingLineAdrressList){
        this.context = context;
        this.movingLineAdrressList = movingLineAdrressList;
    }

    public void makeMovingLineDialog(){

        dialog = new Dialog(context);

        //다이얼로그 배경 투명
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_moving_line);

        ListView listView = (ListView) dialog.findViewById(R.id.movingLineListView);
        MovingLineAdapter movingLineAdapter = new MovingLineAdapter(context,movingLineAdrressList,false);
        listView.setAdapter(movingLineAdapter);

        if(movingLineAdrressList.size()==0){
            ArrayList<AdrressData> emptyList = new ArrayList<>();
            emptyList.add(new AdrressData("현재 주소에는 확진자 동선 정보가 존재하지 않습니다."));
            movingLineAdapter = new MovingLineAdapter(context,emptyList,true);
            listView.setAdapter(movingLineAdapter);
        }

        dialog.show();
    }
}
