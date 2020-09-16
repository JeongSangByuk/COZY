package com.example.cozy.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cozy.Activity.MainActivity;
import com.example.cozy.R;
import com.example.cozy.UI.LoadingDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    public MapView mapView;

    public static GoogleMap mMap;

    private static final String TAG = "google map";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;   // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;   // 0.5초

    boolean needRequest = false;

    private Marker currentMarker = null;

    Location mCurrentLocation;

    public static LatLng currentPosition;

    private Location location;
    public String[] finding = new String[2];   // 0: latitude, 1: longitude;

    public Context context;

    public FusedLocationProviderClient mFusedLocationClient;   // 위치값 획득할 때 필요
    public LocationRequest locationRequest;

    private Address currentAddress;

    private View view;


    @Override
    public void onAttach(@NonNull Context context) {
        Log.d("!!!!!", "onAttach");
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d("!!!!!", "onCreate :");

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("!!!!!", "onCreateView");

        view = inflater.inflate(R.layout.fragment_map, container,false);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();   // 현재 위치 설정 가져오기

        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment)(getActivity()).getSupportFragmentManager().findFragmentById(R.id.map);

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d("!!!!!", "onMapReady :");

        mMap = googleMap;

        // 런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기 전에 지도의 초기 위치를 서울로 이동
        setDefaultLocation();
        startLocationUpdates();

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Log.d(TAG, "onMapClick :");
            }
        });
    }


    // GPS 활성화 확인
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("!!!!!", "onActivityResult :");
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                // 사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {

                    Log.d(TAG, "onActivityResult : GPS 활성화 되어 있음");

                    needRequest = true;

                    return;
                }

                break;
        }
    }

    // 디폴트 위치 설정
    public void setDefaultLocation() {

        Log.d("!!!!!", "setDefaultLocation :");
        // 서울시청
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치 정보 가져올 수 없음";
        String markerSnippet = "GPS 허용 여부를 확인하세요.";

        if (currentMarker != null) {
            currentMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

        Log.d("!!!!!", String.valueOf(currentMarker));
    }


    // 사용자가 GPS 활성 시켰는지 검사
    private boolean checkLocationServicesStatus() {

        Log.d("!!!!!", "checkLocationServicesStatus :");

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }


    // GPS 활성화 관련 메세지 출력
    public void showLocationServiceMessage() {

        Log.d("!!!!!", "showLocationServiceMessage :");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.");
        builder.setCancelable(true);

        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                getActivity().finish();
            }
        });

        builder.create().show();
    }




    // 위치 업데이트 시작
    private void startLocationUpdates() {
        Log.d("!!!!!", "startLocationUpdates :");


        if (!checkLocationServicesStatus()) {
            // 위치 서비스 활성화 되어 있지 않은 경우 안내 메세지
            showLocationServiceMessage();
        }

        else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {   // 퍼미션 없는 경우
                Log.d(TAG, "startLocationUpdates : 퍼미션 가지고 있지 않음");
                return;
            }

            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates (locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission()) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            Log.d("!!!!!", "onLocationResult :");

            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = "내 위치";
                String markerSnippet = getCurrentAddress(currentPosition);


                // 현재 위치에 마커 생성하고 이동
                setCurrentLocationMarker(location, markerTitle, markerSnippet);
                mCurrentLocation = location;
            }


            findLocation(locationResult);
            mFusedLocationClient.removeLocationUpdates(locationCallback);   // 루프 한 번만 돌게 하기

            if(LoadingDialog.loadingDialog != null) {


                LoadingDialog.loadingDialog.dismiss();   // 로딩창 없애기
            }
        }
    };


    // 리버스 지오코더를 이용해 현재 주소 찾기
    private String getCurrentAddress(LatLng latlng) {

        Log.d("!!!!!", "getCurrentAddress: ");

        // 지오코더: GPS를 주소로 변환
        //Geocoder geocoder = new Geocoder(((MainActivity)MainActivity.context), Locale.getDefault());
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;

        Log.d("!!!!!","geocoder" + geocoder);

        try {
            Log.d(TAG, "geocoder");
            addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude,1);
            Log.d("!!!!!","address" + addresses);

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
            currentAddress = addresses.get(0);   // address ex. 대한민국 서울특별시 광진구 군자동 339-1
            return currentAddress.getAddressLine(0);
        }
    }


    // 현재 위치에 마커 생성
    private void setCurrentLocationMarker(Location location, String markerTitle, String markerSnippet) {

        Log.d("!!!!!", "setCurrentLocationMarker :");

        Log.d("!!!!!", markerTitle);
        if (currentMarker != null) {
            currentMarker.remove();
        }

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        // 사용자 주소에서 대한민국 지우기 -> 시도부터 보이게
        markerSnippet = markerSnippet.substring(markerSnippet.indexOf(" ")+1);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);
        //currentMarker.showInfoWindow();   // title 항상 보이게
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);
    }



    // 위도, 경도값 찾기
    private String[] findLocation(LocationResult locationResult) {

        Log.d("!!!!!", "findLocation :");

        List<Location> locationList = locationResult.getLocations();

        if (locationList.size() > 0) {

            location = locationList.get(locationList.size() - 1);

            finding[0] = String.valueOf(location.getLatitude());
            finding[1] = String.valueOf(location.getLongitude());

        }
        Log.i(TAG, finding[0]+ finding[1]);

        return finding;
    }


    // 런타임 퍼미션 처리
    private boolean checkPermission() {

        Log.d("!!!!!", "checkPermission :");

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;
    }
}