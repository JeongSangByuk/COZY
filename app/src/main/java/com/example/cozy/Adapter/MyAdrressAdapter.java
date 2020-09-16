package com.example.cozy.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cozy.Data.AdrressData;
import com.example.cozy.Constant;
import com.example.cozy.Inferface.AdrressDialogListener;
import com.example.cozy.Server.Post;
import com.example.cozy.R;
import com.example.cozy.UI.AdrressDialog;
import com.example.cozy.UI.DeleteAdrressDialog;
import com.example.cozy.UI.MovingLineDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MyAdrressAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<AdrressData> adrressData;
    private FragmentTransaction fragmentTransaction;
    private Fragment currnetFragment;

    public String nowAdrress;
    public ArrayList<AdrressData> movingLineAdrressList;
    public String[] url;

    public MyAdrressAdapter(Context context, ArrayList<AdrressData> data, Fragment currnetFragment, FragmentTransaction ft) {
        this.context = context;
        this.adrressData = data;
        this.fragmentTransaction = ft;
        this.currnetFragment = currnetFragment;

        url = new String[8];

        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return adrressData.size();
    }

    @Override
    public AdrressData getItem(int position) {
        return adrressData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View coverView, ViewGroup viewGroup) {
        View view;
        final TextView textView;
        ImageButton imageButton;

        //주소 등록하기 일 경우
        if (!adrressData.get(position).getAdrressName().equals(Constant.REGISER_MY_ADRRESS)) {

            view = layoutInflater.inflate(R.layout.my_adrress_button, null);
            textView = (TextView) view.findViewById(R.id.myAdrressButton);
            imageButton = (ImageButton) view.findViewById(R.id.deleteAdrressButton);
            textView.setText(adrressData.get(position).getAdrressName(), TextView.BufferType.SPANNABLE);

            String adressString = textView.getText().toString();

            //주소 이름 부분만 크게하는 span
            Spannable span = (Spannable) textView.getText();
            span.setSpan(new RelativeSizeSpan(1.3f), 0, adressString.indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new StyleSpan(Typeface.BOLD), 0, adressString.indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            setButtonClickListener(textView);

            //delete 버튼 처리
            imageButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    DeleteAdrressDialog deleteAdrressDialog = new DeleteAdrressDialog(context, textView.getText().toString().substring(0,
                            textView.getText().toString().indexOf("\n")));
                    deleteAdrressDialog.makeDeleteAdrressDialog();

                    deleteAdrressDialog.setDialogListener(new AdrressDialogListener() {
                        @Override
                        public void onRegisterButtonClicked() {
                            refreshFragment();
                        }
                    });

                }
            });

        } else {
            view = layoutInflater.inflate(R.layout.my_adrress_add_button, null);
            textView = (TextView) view.findViewById(R.id.myAdrressAddButton);
            textView.setText(adrressData.get(position).getAdrressName());
            setButtonClickListener(textView);
        }
        return view;
    }

    //버튼 이벤트리스너 설정하는
    public void setButtonClickListener(final TextView buttonClickInMovingLine) {
        buttonClickInMovingLine.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                String adressString = buttonClickInMovingLine.getText().toString();

                //주소 등록하기 다이얼로그
                if (buttonClickInMovingLine.getText().equals(Constant.REGISER_MY_ADRRESS)) {
                    AdrressDialog adrressDialog = new AdrressDialog(context);
                    adrressDialog.makeAdrressDialog();

                    //인터페이스를 사용하여 콜백메소드 정의
                    adrressDialog.setDialogListener(new AdrressDialogListener() {
                        @Override
                        public void onRegisterButtonClicked() {
                            refreshFragment();
                        }
                    });

                }
                //확진자 동선 정보 보기 다이얼로그
                else {
                    String[] latitudeLogitude = new String[2];

                    //나의 주소 저장
                    nowAdrress = adressString.substring(adressString.indexOf("\n") + 1).replaceAll(" ", "");

                    latitudeLogitude = getLatitudeLongitude(nowAdrress);

                    movingLineAdrressList = new ArrayList<AdrressData>();

                    url[0] = "url";
                    url[1] = Constant.CORONA_MOVING_LINE_URL;
                    url[2] = "kilometer";
                    url[3] = String.valueOf(43);
                    url[4] ="latitude";
                    url[5] = String.valueOf(latitudeLogitude[0]);
                    url[6] ="longitude";
                    url[7] = String.valueOf(latitudeLogitude[1]);

                    connectPost();

                    //날짜 순 정렬
                    movingLineAdrressList.sort(new Comparator<AdrressData>() {
                        @Override
                        public int compare(AdrressData adrressData, AdrressData adrressData1) {

                            return adrressData.getVisitDate().compareTo(adrressData1.getVisitDate());
                        }
                    });

                    MovingLineDialog movingLineDialog = new MovingLineDialog(context, movingLineAdrressList);
                    movingLineDialog.makeMovingLineDialog();
                }
            }
        });
    }

    public void refreshFragment() {
        fragmentTransaction.detach(currnetFragment);
        fragmentTransaction.attach(currnetFragment);
        fragmentTransaction.commit();
    }


    // 지오코더를 이용해 현재 주소 찾기
    private String getCurrentAddress(Double latitude, Double longitude) {

        Address currentAddress;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
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


    // 사용자가 선택한 시군구에 따른 위도와 경도 값 찾기
    private String[] getLatitudeLongitude(String district) {

        Address districtLatitudeLongitude;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> userDistrict;

        String[] latitudeLongitude = new String[2];   // 0: latitude, 1: longitude

        try {
            userDistrict = geocoder.getFromLocationName(district, 1);
        } catch (IOException ioException) {
            // 네트워크 문제
            latitudeLongitude[0] = "지오코더 서비스 사용 불가";
            latitudeLongitude[1] = "no";

            return latitudeLongitude;
        } catch (IllegalArgumentException illegalArgumentException) {

            latitudeLongitude[0] = "잘못된 GPS 좌표";
            latitudeLongitude[1] = "no";

            return latitudeLongitude;
        }

        if (userDistrict == null || userDistrict.size() == 0) {

            latitudeLongitude[0] = "주소 미발견";
            latitudeLongitude[1] = "no";

            return latitudeLongitude;
        } else {
            districtLatitudeLongitude = userDistrict.get(0);
            latitudeLongitude[0] = String.valueOf(districtLatitudeLongitude.getLatitude());
            latitudeLongitude[1] = String.valueOf(districtLatitudeLongitude.getLongitude());

            return latitudeLongitude;
        }
    }


    // POST 방식으로 서버랑 연결
    private void connectPost() {

        Post post = new Post();
        post.execute(url);

        // json으로 string 값 받아오기
        String jsonString = "";
        try {
            jsonString = jsonString + post.get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getCoronaInformation(jsonString);
        Log.d("here",jsonString);
    }


    // 서버에서 확진자 정보 받아오기
    private void getCoronaInformation(String jsonString) {
        String visitDate, latitude, longitude,buildingName;

        try {

            JSONObject jsonObject = new JSONObject(jsonString);

            JSONArray jsonArray = jsonObject.getJSONArray("infecteePaths");

            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject confirmerJSONObject = jsonArray.getJSONObject(index);

                visitDate = confirmerJSONObject.getString("visitDate");
                latitude = confirmerJSONObject.getString("latitude");
                longitude = confirmerJSONObject.getString("longitude");
                buildingName = confirmerJSONObject.getString("buildingName");

                String adrress = getCurrentAddress(Double.parseDouble(latitude), Double.parseDouble(longitude));

                //해당 주소만을 저장.
                if (adrress.replaceAll(" ", "").contains(nowAdrress))
                    movingLineAdrressList.add(new AdrressData(visitDate, latitude, longitude, adrress,buildingName));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
