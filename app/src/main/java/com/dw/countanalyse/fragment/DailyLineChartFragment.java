package com.dw.countanalyse.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dw.countanalyse.AppDatabase;
import com.dw.countanalyse.CountAnalyseApplication;
import com.dw.countanalyse.DatabaseInstance;
import com.dw.countanalyse.R;
import com.dw.countanalyse.entity.DailyStatistic;
import com.dw.countanalyse.util.DateUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyLineChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyLineChartFragment extends Fragment {

    private AppDatabase db;
    private LineChart chart;


    public DailyLineChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * return DailyLineChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyLineChartFragment newInstance() {
        DailyLineChartFragment fragment = new DailyLineChartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_daily_line_chart, container, false);
        chart = root.findViewById(R.id.dailyLineChart);
        db = DatabaseInstance.getDb();
        initChart();
        initChartData();
        return root;
    }

    private void initChart() {
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
    }

    private void initChartData(){
        long today = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.MONTH, -1);
        long lastMonth = calendar.getTimeInMillis();
        getChartData(DateUtil.getDate(calendar.getTime()), DateUtil.getDate(new Date(today)));
    }
    private void getChartData(final String startDate, final String endDate) {
        Log.d("dateRange", startDate + "-" + endDate);
        new Thread(){
            @Override
            public void run() {
                List<DailyStatistic> data = db.recordDAO().queryDailyStatistic(startDate, endDate);
                Message message = handler.obtainMessage();
                message.obj = data;
                handler.sendMessage(message);
            };
        }.start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            List<DailyStatistic> data = (List<DailyStatistic>) msg.obj;
            setChartData(data);
        }
    };
    private void setChartData(final List<DailyStatistic> dailyStatistics){
        List<Entry> avgEntryList = new ArrayList<>();
        List<Entry> maxEntryList = new ArrayList<>();
        List<Entry> countTimesList = new ArrayList<>();
        ValueFormatter xValueFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return dailyStatistics.get((int)value).date;
            }
        };
        int index = 0;
        for (DailyStatistic data : dailyStatistics) {
            Entry avg = new Entry((float) index, data.avg);
            Entry max = new Entry((float) index, data.max);
            Entry countTimes = new Entry((float) index, data.countTimes);
            avgEntryList.add(avg);
            maxEntryList.add(max);
            countTimesList.add(countTimes);
            index ++;
        }
        LineDataSet avgSet = new LineDataSet(avgEntryList, "平均");
        LineDataSet maxSet = new LineDataSet(maxEntryList, "最高");
        LineDataSet countTimesSet = new LineDataSet(countTimesList, "次数");
        avgSet.setColors(new int[]{R.color.chartColor1}, CountAnalyseApplication.getContext());
        maxSet.setColors(new int[]{R.color.chartColor2}, CountAnalyseApplication.getContext());
        countTimesSet.setColors(new int[]{R.color.chartColor3}, CountAnalyseApplication.getContext());
        avgSet.setCircleColors(new int[]{R.color.chartColor1}, CountAnalyseApplication.getContext());
        maxSet.setCircleColors(new int[]{R.color.chartColor2}, CountAnalyseApplication.getContext());
        countTimesSet.setCircleColors(new int[]{R.color.chartColor3}, CountAnalyseApplication.getContext());
        LineData lineData = new LineData(avgSet, maxSet, countTimesSet);
        chart.setData(lineData);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(xValueFormatter);
        chart.invalidate();
    }
}
