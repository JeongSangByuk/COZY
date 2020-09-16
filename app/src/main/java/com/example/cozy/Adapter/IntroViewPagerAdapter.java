package com.example.cozy.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.cozy.Data.IntroImageViewData;
import com.example.cozy.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {

    private View view;
    private Context context;
    private ArrayList<IntroImageViewData> introImageViewDataArrayList;
    public String[] infecteeInformationDate;
    public float[] infecteeNumber;

    public IntroViewPagerAdapter(Context context, ArrayList<IntroImageViewData> introImageViewDataArrayList,
                                 String[] infecteeInformationDate,float[] infceteeNumber) {
        this.context = context;
        this.introImageViewDataArrayList = introImageViewDataArrayList;
        this.infecteeInformationDate = infecteeInformationDate;
        this.infecteeNumber = infceteeNumber;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (position){

            case 0 :
                view = layoutInflater.inflate(R.layout.view_pager_item_first, container, false);
                setFirstIntroViewPager(position);
                break;

            case 1:
                view = layoutInflater.inflate(R.layout.view_pager_item_second, container, false);
                setSecondIntroViewPager(position);
                break;

            case 2:
                view = layoutInflater.inflate(R.layout.view_pager_item_third, container, false);
                setThirdIntroViewPager(position);
                break;
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void setFirstIntroViewPager(int position) {

        TextView textView = (TextView) view.findViewById(R.id.firstViewPagerTextViewItem);
        ImageView imageView = (ImageView) view.findViewById(R.id.firstViewPagerImageViewItem);

        textView.setText(introImageViewDataArrayList.get(position).getIntroText());
        imageView.setImageResource(introImageViewDataArrayList.get(position).getImage());

        Spannable span = (Spannable)textView.getText();
        span.setSpan(new RelativeSizeSpan(2.5f),textView.getText().toString().indexOf("\n") +1
                ,textView.getText().toString().indexOf("\n")+4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new RelativeSizeSpan(0.5f),textView.getText().toString().indexOf("\n")+4,
                textView.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void setSecondIntroViewPager(int postition){
        drawBarChart();
    }

    public void setThirdIntroViewPager(int position){
        TextView textView = (TextView) view.findViewById(R.id.viewPagerTextViewItem);
        ImageView imageView = (ImageView) view.findViewById(R.id.viewPagerImageViewItem);

        textView.setText(introImageViewDataArrayList.get(position).getIntroText());
        imageView.setImageResource(introImageViewDataArrayList.get(position).getImage());
    }

    @SuppressLint("ResourceType")
    public void drawBarChart(){

        //막대 그래프
        BarChart barChart = (BarChart) view.findViewById(R.id.chartInIntro);//layout의 id;

        //확대 불가능
        barChart.setPinchZoom(true);
        barChart.setScaleXEnabled(false);
        barChart.setScaleYEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);

        //barChart.animateY(2000);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextColor(Color.parseColor("#ffffff"));
        xAxis.setGranularity(1f);

        YAxis yAxisL = barChart.getAxisLeft();
        YAxis yAxisR = barChart.getAxisRight();
        yAxisL.setAxisMinimum(0f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(true);
        yAxisL.setDrawAxisLine(false);
        yAxisL.setDrawLabels(false);
        yAxisL.setDrawZeroLine(true);
        yAxisR.setDrawGridLines(false);
        yAxisR.setDrawAxisLine(false);
        yAxisR.setDrawLabels(false);

        //remove horizontal lines
        AxisBase axisBase = barChart.getAxisLeft();
        axisBase.setDrawGridLines(false);

        List<BarEntry> entry_chart = new ArrayList<>();
        BarData barData = new BarData();


        //여기서 일일 확진자수 3일전까지 int값으로 받아오기
        for(int index =0; index <4;index++) {
            entry_chart.add(new BarEntry(index + 1, infecteeNumber[index]));
        }

        BarDataSet barDataSet = new BarDataSet(entry_chart, "");
        barDataSet.setColors(Color.parseColor(context.getString(R.color.newbackgroundorangecolor)));
        barData.addDataSet(barDataSet);
        barDataSet.setValueFormatter(new MyValueFormatter());
        barDataSet.setValueTextColor(Color.parseColor("#ffffff"));
        barChart.setData(barData);
        barChart.setDescription(null);
        barChart.setTouchEnabled(false);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        barData.setValueTextSize(15);
        barChart.setBackgroundColor(Color.parseColor("#00000000"));

        barChart.invalidate();

    }

    public class DayAxisValueFormatter extends ValueFormatter {
        private final BarLineChartBase<?> chart;
        public DayAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value) {
            switch ((int) value){
                case 1 :
                    return infecteeInformationDate[0].substring(5);

                case 2:
                    return infecteeInformationDate[1].substring(5);

                case 3:
                    return infecteeInformationDate[2].substring(5);

                case 4:
                    return infecteeInformationDate[3].substring(5);

                default:
                    return "0";
            }
        }
    }

    public class MyValueFormatter extends ValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,###"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value) {
            // write your logic here
            return mFormat.format(value) + " 명"; // e.g. append a dollar-sign
        }
    }



}
