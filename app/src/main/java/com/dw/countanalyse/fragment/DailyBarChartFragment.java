package com.dw.countanalyse.fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.db.williamchart.data.Frame;
import com.db.williamchart.view.BarChartView;
import com.dw.countanalyse.AppDatabase;
import com.dw.countanalyse.DatabaseInstance;
import com.dw.countanalyse.R;
import com.dw.countanalyse.entity.Record;
import com.dw.countanalyse.entity.TimesCount;
import com.dw.countanalyse.util.DateUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyBarChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyBarChartFragment extends Fragment {
    AppDatabase db;
    BarChart chart;
    private long today;
    private long nextMonth;
    private long janThisYear;
    private long decThisYear;
    private long oneYearForward;
    private Pair<Long, Long> todayPair;
    private Pair<Long, Long> nextMonthPair;
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
        db = DatabaseInstance.getDb();
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
        setDailyBarChart(new Date());

        Button button = root.findViewById(R.id.btnDatePicker);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSettings();
                MaterialDatePicker.Builder<?> builder =setupDateSelectorBuilder();
                MaterialDatePicker<?> picker = builder.build();
                addDatePickerListener(picker);
                picker.show(getChildFragmentManager(), picker.toString());
            }
        });
        return root;
    }
    private void setDailyBarChart(Date date){
        final String sdate = DateUtil.getDate(date);
        Log.i("current date", sdate);
        Thread t = new Thread(){
            @Override
            public void run() {
                Date now = new Date();
                List<TimesCount> records = db.recordDAO().queryTimesCount(sdate);
                Message m = handler.obtainMessage();
                m.obj = records;
                Bundle b = new Bundle();
                b.putString("date", sdate);
                m.setData(b);
                handler.sendMessage(m);
            }
        };
        t.start();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            List<TimesCount> timesCounts = (List<TimesCount>) msg.obj;
            Bundle b = msg.getData();
            String date = b.getString("date");
            setData(timesCounts, date);
        }
    };
    private MaterialDatePicker.Builder<?> setupDateSelectorBuilder(){
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setSelection(today);
        return builder;
    }
    private void setData(List<TimesCount> timesCounts, String date) {
        List<BarEntry> values = new ArrayList<>();
        for (TimesCount tc : timesCounts) {
            values.add(new BarEntry((float) tc.times, (float) tc.count));
        }
        BarDataSet set = new BarDataSet(values, date);
        set.setDrawIcons(false);
        BarData data = new BarData(set);
        chart.setData(data);
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
    }

    private void initSettings() {
        today = MaterialDatePicker.todayInUtcMilliseconds();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(today);
        calendar.roll(Calendar.MONTH, 1);
        nextMonth = calendar.getTimeInMillis();

        calendar.setTimeInMillis(today);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        janThisYear = calendar.getTimeInMillis();
        calendar.setTimeInMillis(today);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        decThisYear = calendar.getTimeInMillis();

        calendar.setTimeInMillis(today);
        calendar.roll(Calendar.YEAR, 1);
        oneYearForward = calendar.getTimeInMillis();

        todayPair = new Pair<>(today, today);
        nextMonthPair = new Pair<>(nextMonth, nextMonth);
    }

    private void addDatePickerListener(MaterialDatePicker picker) {
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Long timestamp = (Long) selection;
                setDailyBarChart(new Date(timestamp));
            }
        });
    }
}
