package com.example.cozy.Fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cozy.Data.AdrressData;
import com.example.cozy.Adapter.MyAdrressAdapter;
import com.example.cozy.Data.Database;
import com.example.cozy.R;

import java.util.ArrayList;
import java.util.Collections;

public class MovingLineFragment extends Fragment {

    public View view;
    public TextView addAdrressButton;
    private SQLiteDatabase database;
    private Integer dataCount;
    public ArrayList<AdrressData> adrressList;
    public ArrayList<AdrressData> movingLineAdrressList;

    public String[] url = new String[4];
    String[] temp = new String[2];


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_moving_line, container, false);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Fragment currnetFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

        database = Database.getInstance(getActivity()).getReadableDatabase();
        dataCount = Database.getInstance(getActivity()).getDataCount(database);

        //데이터베이스의 정보 개수만큼 리스트 생성
        adrressList = new ArrayList<AdrressData>(dataCount);

        for (int index = 0; index < dataCount + 1; index++) {
            adrressList.add(new AdrressData(Database.getInstance(getActivity()).getAdrress(database, index)));
        }

        ListView listView = (ListView) view.findViewById(R.id.adrressButtonListView);
        MyAdrressAdapter myAdrressAdapter = new MyAdrressAdapter(getActivity(), adrressList, currnetFragment, fragmentTransaction);
        listView.setAdapter(myAdrressAdapter);

    }

}
