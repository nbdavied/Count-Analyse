package com.dw.countanalyse.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.AttrRes;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

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
    private Button btnDatePicker;
    private Pair<Long, Long> dateSelection;


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
        btnDatePicker = root.findViewById(R.id.btnLineChartDatePicker);
        db = DatabaseInstance.getDb();
        initSettings();
        initChart();
        initChartData();
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<?> builder =setupDateSelectorBuilder();
                //builder.setTheme(resolveOrThrow(getContext(), R.attr.materialCalendarTheme));
                MaterialDatePicker<?> picker = builder.build();
                addDatePickerListener(picker);
                picker.show(getChildFragmentManager(), picker.toString());
            }
        });
        return root;
    }

    private MaterialDatePicker.Builder<?> setupDateSelectorBuilder() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();
        builder.setSelection(dateSelection);
        return builder;
    }

    private void addDatePickerListener(MaterialDatePicker<?> picker) {
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Object>() {
            @Override
            public void onPositiveButtonClick( Object selection) {
                dateSelection = (Pair<Long, Long>) selection;
                getChartData();
            }
        });
    }

    private void initSettings() {
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.MONTH, -1);
        dateSelection = new Pair<>(calendar.getTimeInMillis(), now);
    }

    private void initChart() {
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
    }

    private void initChartData(){
        long today = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.MONTH, -1);
        getChartData();
    }
    private void getChartData() {
        final String startDate = DateUtil.getDate(new Date(dateSelection.first));
        final String endDate = DateUtil.getDate(new Date(dateSelection.second));
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
                int index = (int)value;
                if (index < 0 || index >= dailyStatistics.size()) {
                    return "";
                }
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

    private static int resolveOrThrow(Context context, @AttrRes int attributeResId) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attributeResId, typedValue, true)) {
            return typedValue.data;
        }
        throw new IllegalArgumentException(context.getResources().getResourceName(attributeResId));
    }
}
