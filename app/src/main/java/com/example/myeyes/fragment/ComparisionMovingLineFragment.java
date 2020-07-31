package com.example.myeyes.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myeyes.Post;
import com.example.myeyes.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ComparisionMovingLineFragment extends Fragment {

    RadioButton radioButton1, radioButton2, radioButton3;
    RadioGroup radioGroup;

    Circle circle;

    String[] url = new String[4];

    Marker confirmerMarker = null;
    LatLng confirmerLatLng;
    Address confirmerAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comparision_moving_line, container, false);

        radioButton1 = (RadioButton) view.findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) view.findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) view.findViewById(R.id.radioButton3);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        /*
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("@@@", "그룹");

                switch (checkedId) {
                    case R.id.radioButton1:
                        Log.d("@@@", "111");
                        nearKilometer(1000);
                        break;
                    case R.id.radioButton2:
                        Log.d("@@@", "222");
                        nearKilometer(2000);
                        break;
                    case R.id.radioButton3:
                        Log.d("@@@", "333");
                        nearKilometer(3000);
                        break;
                }
            }
        });
         */


        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("!!!!!", "11");
                drawCircle(1000, 14);
            }
        });

        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("!!!!!", "22");
                drawCircle(2000, 13);
            }
        });

        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("!!!!!", "33");
                drawCircle(3000, 12);
            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("!!!!!", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);


        url[0] = "http://ec2-13-209-74-229.ap-northeast-2.compute.amazonaws.com:3000/search";

        connectPost();

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragmentGoogle, new MapFragment());
        fragmentTransaction.commit();
    }


    // 선택한 km에 따라 circle 추가
    private void drawCircle(int radius, int zoom) {
        Log.d("!!!!!", "addCircle");

        MapFragment.mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));

        if(circle != null) {
            circle.remove();
        }

        CircleOptions kilometer = new CircleOptions().center(MapFragment.currentPosition)   // 원점
                .radius(radius)   // 반지름 (단위: m)
                .strokeWidth(0f)   // 선 너비: 0f(선 없음)
                .fillColor(Color.parseColor("#4C880000"));   // 배경색, 4C는 투명도 30%

        circle = MapFragment.mMap.addCircle(kilometer);

        url[1] = String.valueOf(radius/1000);
        url[2] = String.valueOf(MapFragment.currentPosition.latitude);
        url[3] = String.valueOf(MapFragment.currentPosition.longitude);

        for(int i=0; i<4; i++)
        {
            Log.d("!!!!!", "i"+i+url[i]);
        }
        connectPost();
    }


    // POST 방식으로 서버랑 연결
    private void connectPost() {

        Log.d("!!!!!","connectPost");

        Post post = new Post();
        post.execute(url);

        // json으로 string 값 받아오기
        String jsonString ="";
        try {
            Log.d("!!!!!","json"+jsonString);

            jsonString= jsonString+post.get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getCoronaInformation(jsonString);
    }


    // 서버에서 확진자 정보 받아오기
    private void getCoronaInformation(String jsonString) {
        String visitDate, latitude, longitude;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            JSONArray jsonArray = jsonObject.getJSONArray("infecteePaths");

            if (confirmerMarker != null) {
                confirmerMarker.remove();
            }


            for(int index = 0; index < jsonArray.length(); index++)
            {
                JSONObject confirmerJSONObject = jsonArray.getJSONObject(index);

                visitDate = confirmerJSONObject.getString("visitDate");
                latitude = confirmerJSONObject.getString("latitude");
                longitude = confirmerJSONObject.getString("longitude");

                confirmerLatLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));

                String address = getConfirmerAddress(Double.valueOf(latitude), Double.valueOf(longitude));
                drawMarker(visitDate, address);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 지오코더를 이용해 현재 주소 찾기
    private String getConfirmerAddress(Double latitude, Double longitude) {
        Log.d("!!!!!", "getCurrentAddress :");

        //지오코더: GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;

        try {
            Log.d("!!!!!", "geocoder");
            addresses = geocoder.getFromLocation(latitude, longitude,1);
        }
        catch (IOException ioException) {
            //네트워크 문제
            //Toast.makeText(this, "지오코더 서비스 사용 불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용 불가";
        }
        catch (IllegalArgumentException illegalArgumentException) {
            //Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }


        if (addresses == null || addresses.size() == 0) {
            Log.d("!!!!!", "사이즈"+String.valueOf(addresses.size()));

            //Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        else {
            confirmerAddress = addresses.get(0);   // address ex. 대한민국 서울특별시 광진구 군자동 339-1

            return confirmerAddress.getAddressLine(0);
        }
    }

    // 확진자 마커 그리기
    private void drawMarker(String visitDate, String address) {

        String markerTitle, markerSnippet;

        markerTitle = visitDate;
        markerSnippet = address;
        markerSnippet = markerSnippet.substring(markerSnippet.indexOf(" ")+1);
        markerSnippet = markerSnippet.substring(markerSnippet.indexOf(" ")+1);

        Log.d("!!!!!","markerSnippet!="+markerSnippet );

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(confirmerLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));   // 마커 색상 변경
        markerOptions.alpha(0.5f);   // 투명도 지정하기

        confirmerMarker = MapFragment.mMap.addMarker(markerOptions);
    }
}
