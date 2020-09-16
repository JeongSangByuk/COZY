package com.example.cozy.SpeakingAPI;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.cozy.Constant;
import com.example.cozy.Fragment.MapFragment;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class STTAPI implements SpeechRecognizeListener {
    public String sstString, chatbotString;
    private MainActivity mainActivity;
    private MikeDialog mikeDialog;
    public Boolean isCancled=false;

    String[] forwardToServer = new String[8];
    String[] areaInformation = new String[8];

    String sido, sigoongu;


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

                if (!isCancled){
                    mikeDialog.mikeTextView.setText("잘 들리지 않거나 오류가 발생했어요!\nCOZY 에게 말해보세요!");
                    readChatbotMessage("다시 말해보세요.");
                }
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
                connectServer(forwardToServer);
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
    private void connectServer(String[] server) {

        Log.d("!!!!!", "connectPost");

        Post post = new Post();
        post.execute(server);

        // json으로 string 값 받아오기
        String jsonString = "";

        try {
            Log.d("!!!!!", "json" + jsonString);

            jsonString= jsonString + post.get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(server[1].contains("danbee")) {

            classificateQuestion(jsonString);
        }
        else
        {
            getCoronaInformation(jsonString);
        }

    }

    // 사용자 입력 분류하기
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void classificateQuestion (String jsonString) {

        Log.d("!!!!!","classificateQuestion");
        String type;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            type = jsonObject.getString("type");

            Log.d("!!!!!","jsonobject"+jsonObject);

            Log.d("!!!!!","type"+type);


            switch (type) {
                case "코로나정보":
                    getServerCoronaInformation(jsonObject);
                    break;
                case "확진자동선":
                    getServerInformationMovingLine(jsonObject);
                    break;
                case "동선비교":
                    getServerComparisionMovingLine(jsonObject);
                    break;
            }
        } catch (JSONException e) {

            Log.d("!!!!! here","JSONException");
            e.printStackTrace();
        }
    }


    // 서버에서 받은 정보 - 코로나정보
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getServerCoronaInformation(JSONObject jsonObject) {

        Log.d("!!!!! ","getServerCoronaInformation");

        String message;

        try {
            message = jsonObject.getString("message");
        } catch (Exception e){
            onError(0000,"NoValue");
            Log.d("noValueError","NoValueError");
            e.printStackTrace();
            return;
        }

        Log.d("!!!!!","message :"+message);

        if(message.contains("이런 의미")) {
            message = "제가 도움을 드리지 못하는 문제입니다. 다시 말씀해주세요.";
        }

        Log.d("!!!!!","message :"+message);

        readChatbotMessage(message);

    }


    // 서버에서 받은 정보 - 확진자동선
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getServerInformationMovingLine(JSONObject jsonObject) {

        Log.d("!!!!!~~~~~","getServerInformationMovingLine :");

        try {

            sido = jsonObject.getString("sido");
            sigoongu = jsonObject.getString("sigoongu");

            Log.d("!!!!!","sido :"+sido);
            Log.d("!!!!!","sigoongu :"+sigoongu);

            //getAddress(sido + sigoongu);


            if(!sido.equals("") && !sigoongu.equals("")) {

                sido = jsonObject.getString("sido");
                sigoongu = jsonObject.getString("sigoongu");

                Log.d("!!!!!","sido :"+sido);
                Log.d("!!!!!","sigoongu :"+sigoongu);

                getAddress(sido + sigoongu);

            }
            else if (sido.equals("") || sigoongu.equals("")) {   // sido, sigoongu 비어 있는 경우

                readChatbotMessage("지역명을 더 자세히 말씀해주세요.");
            }

            else {
                readChatbotMessage("해당 지역에는 확진자가 존재하지 않습니다.");
            }


        } catch (JSONException e) {

            Log.d("!!!!! here","JSONException");
            e.printStackTrace();
        }
    }


    // 서버에서 받은 정보 - 동선비교
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getServerComparisionMovingLine(JSONObject jsonObject) {

        Log.d("!!!!!","getServerComparisionMovingLine :");
        String visitDate, latitude, longitude, buildingName;

        try {
            String markerSnippet;

            //확진자 동선 마커리스트와 총값 초기화
            String wholeMarkerList = "";

            int markerCount = 0;

            //마커를 저장할 리스트 생성
            List<String> markerList = new ArrayList<>();

            JSONArray jsonArray = jsonObject.getJSONArray("infecteePaths");


            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject confirmerJSONObject = jsonArray.getJSONObject(index);

                visitDate = confirmerJSONObject.getString("visitDate");
                latitude = confirmerJSONObject.getString("latitude");
                longitude = confirmerJSONObject.getString("longitude");
                buildingName = confirmerJSONObject.getString("buildingName");

                String address = getCurrentAddress(Double.parseDouble(latitude), Double.parseDouble(longitude));


                markerSnippet = address;
                markerSnippet = markerSnippet.substring(markerSnippet.indexOf(" ")+1);
                markerSnippet = markerSnippet.substring(markerSnippet.indexOf(" ")+1);

                if(buildingName != null) {
                    markerList.add(getDateString(visitDate,markerCount) +" "+markerSnippet + ", " + buildingName+ "\n");
                }
                else {
                    markerList.add(getDateString(visitDate,markerCount) +" "+markerSnippet + "\n");
                }

                markerCount++;

            }

            for(Object object : markerList){

                Log.d("!!!!!","object:   "+ object);
                wholeMarkerList = wholeMarkerList + object.toString();
            }


            if(wholeMarkerList.equals("")){
                wholeMarkerList = "확진자가 존재하지 않습니다.";
            }
            else
            {
                wholeMarkerList = wholeMarkerList + " 이상입니다.";
            }


            readChatbotMessage(wholeMarkerList);


        } catch (JSONException e) {

            Log.d("!!!!! here","JSONException");
            e.printStackTrace();
        }
    }

    // 지오코더를 이용해 위도,경도 찾기
   @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
   private String getAddress(String sidoSigoongu) {

        Log.d("!!!!!", "getCurrentAddress :");

        Log.d("!!!!!", "sidoSigoongu :"+ sidoSigoongu);

        List<Address> addresses = null;

        String[] location = new String[2];

        //지오코더: GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(mainActivity, Locale.getDefault());

        try {
            Log.d("!!!!!", "geocoder");
            addresses = geocoder.getFromLocationName(sidoSigoongu, 10);

            Log.d("!!!!!", "addresses :"+ addresses);
        }
        catch (IOException ioException) {
            //네트워크 문제
            return "지오코더 서비스 사용 불가";

        }
        catch (IllegalArgumentException illegalArgumentException) {
            return"잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Log.d("!!!!!", "사이즈"+String.valueOf(addresses.size()));
            return "주소 미발견";
        }

        else {
            Address address = addresses.get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();

            areaInformation[0] = "url";
            areaInformation[1] = "http://ec2-13-209-74-229.ap-northeast-2.compute.amazonaws.com:3000/search";
            areaInformation[2] = "kilometer";
            areaInformation[3] = String.valueOf(43);
            areaInformation[4] = "latitude";
            areaInformation[5] = String.valueOf(latitude);
            areaInformation[6] = "longitude";
            areaInformation[7] = String.valueOf(longitude);

            connectServer(areaInformation);
            return "";
        }
    }


    // 서버에서 확진자 정보 받아오기 (확진자동선)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getCoronaInformation(String jsonString) {

        Log.d("!!!!!~~~~~", "getCoronaInformation : ");

        String visitDate, latitude, longitude, buildingName;

        Log.d("!!!!!~~~~~", "jsonString : "+jsonString);

        try {
            String markerSnippet;

            //확진자 동선 마커리스트와 총값 초기화
            String wholeMarkerList = "";

            int markerCount = 0;

            //마커를 저장할 리스트 생성
            List<String> markerList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(jsonString);

            JSONArray jsonArray = jsonObject.getJSONArray("infecteePaths");


            for (int index = 0; index < jsonArray.length(); index++) {

                JSONObject confirmerJSONObject = jsonArray.getJSONObject(index);

                visitDate = confirmerJSONObject.getString("visitDate");
                latitude = confirmerJSONObject.getString("latitude");
                longitude = confirmerJSONObject.getString("longitude");
                buildingName = confirmerJSONObject.getString("buildingName");

                String address = getCurrentAddress(Double.parseDouble(latitude), Double.parseDouble(longitude));

                markerSnippet = address;
                markerSnippet = markerSnippet.substring(markerSnippet.indexOf(" ")+1);
                markerSnippet = markerSnippet.substring(markerSnippet.indexOf(" ")+1);
                Log.d("!!!!!","address : "+address);


                // 해당 주소만을 저장
                if (address.replaceAll(" ", "").contains(sido + sigoongu))
                {
                    //movingLineAdrressList.add(new AdrressData(visitDate, latitude, longitude, address, buildingName));


                    if(buildingName != null) {
                        markerList.add(getDateString(visitDate,markerCount) +" "+markerSnippet + ", " + buildingName+ "\n");
                    }
                    else {
                        markerList.add(getDateString(visitDate,markerCount) +" "+markerSnippet + "\n");
                    }

                    markerCount++;

                }
            }

            Log.d("!!!!!","markerList:  "+ String.valueOf(markerList));

            for(Object object : markerList){

                Log.d("!!!!!","object:   "+ object);
                wholeMarkerList = wholeMarkerList + object.toString();
            }

            if(wholeMarkerList.equals("")){
                wholeMarkerList = "확진자가 존재하지 않습니다.";
            }
            else
            {
                wholeMarkerList = sido + " "+ sigoongu + " " + "확진자 동선 정보입니다." + wholeMarkerList;
                wholeMarkerList = wholeMarkerList + " 이상입니다.";
            }


            Log.d("!!!!!","whole :"+wholeMarkerList);

            readChatbotMessage(wholeMarkerList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // 날짜 정보 변환
    public String getDateString(String inputtedString,int markerCount){
        int month,day;
        String countString="";
        inputtedString = inputtedString.replaceAll(" ","");

        if(markerCount<11){
            countString = Constant.NUMBER_KOKEAN_1[markerCount];
        }
        else{
            countString = countString + Constant.NUMBER_KOKEAN_3[markerCount/10 - 1] + Constant.NUMBER_KOKEAN_2[markerCount % 10 - 1];
        }

        month = Integer.parseInt(inputtedString.substring(5,7));
        day = Integer.parseInt(inputtedString.substring(8,10));

        return countString + " 번째 확진자 동선 정보, " + month + "월 " + month  + "일,";
    }


    // 지오코더를 이용해 현재 주소 찾기 (위도,경도 -> 주소)
    private String getCurrentAddress(Double latitude, Double longitude) {

        Address currentAddress;

        Geocoder geocoder = new Geocoder(mainActivity, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

        } catch (IOException ioException) {
            //네트워크 문제
            return "지오코더 서비스 사용 불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            return "잘못된 GPS 좌표";
        }


        if (addresses == null || addresses.size() == 0) {

            return "주소 미발견";

        } else {
            currentAddress = addresses.get(0);   // address ex. 대한민국 서울특별시 광진구 군자동 339-1
            return currentAddress.getAddressLine(0);
        }
    }


    public void readChatbotMessage(String message) {

        Log.d("!!!!!","readChatbotMessage");

        chatbotString = message;

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mainActivity.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        Log.d("!!!!!", "wwwwwwwwww");
                        mikeDialog.startTTS(chatbotString);
                    }
                });

            }
        }, 500);
    }
}
