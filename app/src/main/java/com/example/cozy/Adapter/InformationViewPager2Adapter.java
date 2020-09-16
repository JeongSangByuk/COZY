package com.example.cozy.Adapter;

import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cozy.Activity.MainActivity;
import com.example.cozy.R;

public class InformationViewPager2Adapter extends RecyclerView.Adapter<InformationViewPager2Adapter.ViewHolder> {

    private View view;
    private Context context;
    private LayoutInflater layoutInflater;
    public MainActivity mainActivity;
    ;

    public InformationViewPager2Adapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public InformationViewPager2Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (viewType) {

            case 0:
                view = layoutInflater.inflate(R.layout.view_pager_information_first, parent, false);
                setFirstInformationViewPager();
                break;

            case 1:
                view = layoutInflater.inflate(R.layout.view_pager_information_second, parent, false);
                break;

            case 2:
                view = layoutInflater.inflate(R.layout.view_pager_information_third, parent, false);
                break;

            case 3:
                view = layoutInflater.inflate(R.layout.view_pager_information_fourth, parent, false);
                break;

            case 4:
                view = layoutInflater.inflate(R.layout.view_pager_information_fifth, parent, false);
                break;

            case 5:
                view = layoutInflater.inflate(R.layout.view_pager_information_sixth, parent, false);
                break;

        }

        return new ViewHolder(view);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull InformationViewPager2Adapter.ViewHolder holder, int position) {
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public void setFirstInformationViewPager() {
        TextView textView = view.findViewById(R.id.title_first_information_view_pager);

        Spannable span = (Spannable) textView.getText();
        span.setSpan(new RelativeSizeSpan(1.5f), textView.getText().toString().indexOf("\n") + 1
                , textView.getText().toString().indexOf("\n") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.newbackgroundorangecolor)),
                textView.getText().toString().indexOf("C"), textView.getText().toString().indexOf("C") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
