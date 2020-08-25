package com.example.cozy.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cozy.Activity.MainActivity;
import com.example.cozy.BackPressCloseHandler;
import com.example.cozy.Constant;
import com.example.cozy.Inferface.OnBackPressedListener;
import com.example.cozy.Server.Get;
import com.example.cozy.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CoronaInformationFragment extends Fragment  {

    private View view;
    public String jsonString = "";
    public String temporaryString,liveDate,overseasInflowNumber,nationalOccurenceNumber,totalInfecteeNumber,accumulationInfecteeNumber,casualtyNumber;

    private String[] forwardToServer = new String[2];

    String infectee[][] = new String [19][2];
    // 0: region, 1: infecteeNumber

    PieChart pieChart;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_corona_information,container,false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        // GET 방식으로 서버 연결
        connectGetConfirmer();
        connectGetRegion();

        super.onActivityCreated(savedInstanceState);

    }


    private void connectGetConfirmer () {

        Get getConfirmer = new Get();

        getConfirmer.execute(Constant.CORONA_INFORMATION_URL);

        //json으로 string 값 받아오기
        String jsonString ="";
        try {
            jsonString= jsonString+ getConfirmer.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getCoronaInformation(jsonString);

        setConfirmerInformation();
    }


    // 확진자 정보 나타내기
    private void setConfirmerInformation() {

        //setText로 동적으로 text 준다.
        TextView liveDateTextView = (TextView)view.findViewById(R.id.liveDate);
        liveDateTextView.setText(liveDate);

        //텍스트뷰 글자 크기 가변적 적용
        TextView upTextView = (TextView)view.findViewById(R.id.upTextView);
        temporaryString = "일일 확진자 수 : "+ totalInfecteeNumber +"명 \n(국내 확진 " + nationalOccurenceNumber + " + 해외 유입 " + overseasInflowNumber +")";
        upTextView.setText(temporaryString);
        Spannable span = (Spannable)upTextView.getText();
        span.setSpan(new RelativeSizeSpan(1.7f), 11,getStringEndIndex(upTextView.getText().toString()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //텍스트뷰 글자 크기 가변적 적용
        TextView downTextView = (TextView)view.findViewById(R.id.downTextView);
        temporaryString = "누적 확진자 수 : " + accumulationInfecteeNumber + "명 \n누적 사망자 수 : "+ casualtyNumber + "명";
        downTextView.setText(temporaryString);

        span = (Spannable)downTextView.getText();
        span.setSpan(new RelativeSizeSpan(1.7f), 11,getStringEndIndex(downTextView.getText().toString()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new RelativeSizeSpan(1.7f),getStringStartIndex(downTextView.getText().toString()),downTextView.getText().toString().length()
                ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    //동적으로 크기를 바꾸기위해 '명'이 나올때의 인덱스를 구함
    public int getStringEndIndex(String inputtedString){

        for(int index = 0; index<inputtedString.length();index++){
            if(inputtedString.charAt(index) == '명')
                return index+1;
        }
        return 0;
    }


    //사망자수 크기를 동적으로 바꾸기위해 뒤에서부터 인덱스 구함.
    public int getStringStartIndex(String inputtedString){
        for(int index = inputtedString.length()-1; index>0;index--){
            if(inputtedString.charAt(index) == ':')
                return index+2;
        }
        return 0;
    }


    // 코로나 정보 가져오기
    public void getCoronaInformation(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            liveDate = jsonObject.getString("liveDate");

            JSONObject infecteeInformationJSONObject = jsonObject.getJSONObject("dailyInfectee");
            nationalOccurenceNumber = infecteeInformationJSONObject.getString("nationalOccurence");
            overseasInflowNumber = infecteeInformationJSONObject.getString("overseasInflow");
            int temporaryTotalInfecteeNumber = Integer.parseInt(nationalOccurenceNumber) + Integer.parseInt(overseasInflowNumber);
            totalInfecteeNumber = Integer.toString( temporaryTotalInfecteeNumber);

            JSONArray jsonArray = jsonObject.getJSONArray("liveState");
            JSONObject accumulationInfecteeJSONObject = jsonArray.getJSONObject(0);
            accumulationInfecteeNumber = accumulationInfecteeJSONObject.getString("data");
            accumulationInfecteeJSONObject = jsonArray.getJSONObject(3);
            accumulationInfecteeNumber = accumulationInfecteeNumber.substring(4);
            casualtyNumber = accumulationInfecteeJSONObject.getString("data");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // POST 방식으로 서버랑 연결
    private void connectGetRegion() {

        Log.d("!!!!!","connectGetRegion");

        forwardToServer[0] = "http://ec2-13-209-74-229.ap-northeast-2.compute.amazonaws.com:3000/regionalCorona";


        Get getRegion = new Get();
        getRegion.execute(forwardToServer);

        // json으로 string 값 받아오기
        String jsonString = "";

        try {
            Log.d("!!!!!","json"+jsonString);

            jsonString= jsonString + getRegion.get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getCoronaRegionalData(jsonString);

    }


    // 지역별 확진자 정보 서버로부터 가지고 오기
    private void getCoronaRegionalData(String jsonString) {
        Log.d("!!!!!","getCoronaRegionalData");

        try {

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("regionallist");

            for(int index = 0; index < jsonArray.length(); index++)
            {
                JSONObject confirmerJSONObject = jsonArray.getJSONObject(index);

                infectee[index][0] = confirmerJSONObject.getString("region");
                infectee[index][1] = confirmerJSONObject.getString("infecteeNum");
            }

            setPieChart();   // 파이 차트 그리기

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // 파이차트 만들기
    private void setPieChart() {

        Log.d("!!!!!","setPieChart");

        pieChart = (PieChart) view.findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(7,7,7,7);   // 간격 띄우기
        pieChart.setDrawCenterText(true);
        pieChart.setDragDecelerationFrictionCoef(0.2f);   // 드래그해서 얼마나 돌아가게 할까
        //pieChart.setTouchEnabled(false);
        pieChart.setRotationEnabled(false);   // 그래프 회전 여부
        pieChart.setDrawHoleEnabled(true);   // 가운데 원형 가능 여부
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(35f);   // 가운데 원형 반지름
        pieChart.setTransparentCircleRadius(35f);   // 불투명한 원형 부분 설정할 수 있음
        pieChart.setEntryLabelColor(Color.BLACK);   // 차트 항목별 제목 색깔
        pieChart.getLegend().setEnabled(false);   // 범례 없애기
        //pieChart.setUsePercentValues(true);
        //pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); // 애니메이션

        ArrayList city = new ArrayList();

        for(int index = 4; index >=0 ; index--) {
            city.add(new PieEntry(Float.parseFloat(infectee[index][1]), infectee[index][0]));
        }

        PieDataSet dataSet = new PieDataSet(city,"Cities");   // 항목
        dataSet.setSliceSpace(3f);   // 차트들 사이 간격
        dataSet.setSelectionShift(3f);   // 클릭하면 커짐
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);   // 항목 이름 밖으로
        ///dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);   // 퍼센트 밖으로
        //dataSet.setValueLineColor(ColorTemplate.COLOR_NONE);   // 설명하는 선 없애기
        dataSet.setValueLinePart1OffsetPercentage(100f);   // When valuePosition is OutsideSlice, indicates offset as percentage out of the slice size
        dataSet.setValueLinePart1Length(0.6f);   // When valuePosition is OutsideSlice, indicates length of first half of the line
        dataSet.setValueLinePart2Length(0.2f);   // When valuePosition is OutsideSlice, indicates length of second half of the line
        dataSet.setValueTextSize(10f);

        PieData data = new PieData(dataSet);   // 퍼센트
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(8.5f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
    }
}
