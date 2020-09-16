package com.example.cozy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cozy.Data.AdrressData;
import com.example.cozy.R;

import java.util.ArrayList;

public class MovingLineAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private boolean isEmpty;
    public ArrayList<AdrressData> movingLineAdrressList;

    public MovingLineAdapter(Context context, ArrayList<AdrressData> movingLineAdrressList, boolean isEmpty) {
        this.context = context;
        this.movingLineAdrressList = movingLineAdrressList;
        this.isEmpty = isEmpty;

        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return movingLineAdrressList.size();
    }

    @Override
    public Object getItem(int position) {
        return movingLineAdrressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View coverView, ViewGroup viewGroup) {

        View view;
        TextView textView;
        ImageView imageView;

        view = layoutInflater.inflate(R.layout.moving_line_list_item, null);
        textView = (TextView) view.findViewById(R.id.movingLineListItem);
        if(!isEmpty)
            textView.setText(movingLineAdrressList.get(position).getVisitDate() + "\n" + movingLineAdrressList.get(position).getAdrress()
                    + " " + movingLineAdrressList.get(position).getBuildingName());
        else {
            imageView = view.findViewById(R.id.pinImage);
            imageView.setImageDrawable(null);
            textView.setText(movingLineAdrressList.get(position).getAdrressName());
        }
        return view;
    }
}
