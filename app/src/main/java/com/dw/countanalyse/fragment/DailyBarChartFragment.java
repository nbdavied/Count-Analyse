package com.dw.countanalyse.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.williamchart.data.Frame;
import com.db.williamchart.view.BarChartView;
import com.dw.countanalyse.AppDatabase;
import com.dw.countanalyse.R;
import com.dw.countanalyse.entity.Record;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyBarChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyBarChartFragment extends Fragment {
    AppDatabase db;
    BarChart chart;

    public DailyBarChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment DailyBarChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyBarChartFragment newInstance() {
        DailyBarChartFragment fragment = new DailyBarChartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_daily_bar_chart, container, false);
        db = Room.databaseBuilder(getContext(),
                AppDatabase.class, "record-db").build();
        chart = root.findViewById(R.id.dailyBarChart);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        Thread t = new Thread(){
            @Override
            public void run() {

                List<Record> records = db.recordDAO().queryRecordBetweenDate("20210101", "20991231");
                Message m = handler.obtainMessage();
                m.obj = records;
                handler.sendMessage(m);
            }
        };
        t.start();
        return root;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            List<Record> records = (List<Record>) msg.obj;
            setData(records);
        }
    };

    private void setData(List<Record> records) {
        List<BarEntry> values = new ArrayList<>();
        for (Record r : records) {
            Log.d("times", r.times + "");
            values.add(new BarEntry((float) r.id, (float) r.times));
        }
        BarDataSet set = new BarDataSet(values, "today");
        BarData data = new BarData(set);
        chart.setData(data);
        chart.notifyDataSetChanged();
    }
}
