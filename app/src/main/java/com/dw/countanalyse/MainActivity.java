package com.dw.countanalyse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dw.countanalyse.entity.Record;
import com.dw.countanalyse.util.DateUtil;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String MAX = "max";
    private final static String TODAY_MAX = "todayMax";
    private final static String TODAY_COUNT = "todayCount";
    TextView tvShowCount;
    TextView tvMax;
    TextView tvTodayMax;
    TextView tvTodayCount;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnKey0 = findViewById(R.id.btnKey0);
        btnKey0.setOnClickListener(this);
        Button btnKey1 = findViewById(R.id.btnKey1);
        btnKey1.setOnClickListener(this);
        Button btnKey2 = findViewById(R.id.btnKey2);
        btnKey2.setOnClickListener(this);
        Button btnKey3 = findViewById(R.id.btnKey3);
        btnKey3.setOnClickListener(this);
        Button btnKey4 = findViewById(R.id.btnKey4);
        btnKey4.setOnClickListener(this);
        Button btnKey5 = findViewById(R.id.btnKey5);
        btnKey5.setOnClickListener(this);
        Button btnKey6 = findViewById(R.id.btnKey6);
        btnKey6.setOnClickListener(this);
        Button btnKey7 = findViewById(R.id.btnKey7);
        btnKey7.setOnClickListener(this);
        Button btnKey8 = findViewById(R.id.btnKey8);
        btnKey8.setOnClickListener(this);
        Button btnKey9 = findViewById(R.id.btnKey9);
        btnKey9.setOnClickListener(this);
        Button btnEnter = findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(this);
        tvShowCount = findViewById(R.id.tvShowCount);
        tvShowCount.setOnClickListener(this);
        tvMax = findViewById(R.id.tvMax);
        tvTodayMax = findViewById(R.id.tvTodayMax);
        tvTodayCount = findViewById(R.id.tvCountToday);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "record-db").build();
        countData();
    }

    @Override
    public void onClick(View v) {
        String btnName = getName(v.getId());
        if (btnName.startsWith("btnKey")) {
            String clickDigit = btnName.substring(6);
            String count = tvShowCount.getText().toString();
            int iCount = Integer.parseInt(count + clickDigit);
            tvShowCount.setText(String.valueOf(iCount));
            return;
        }
        if (v.getId() == R.id.tvShowCount) {
            tvShowCount.setText("0");
            return;
        }
        if (v.getId() == R.id.btnEnter) {
            int count = Integer.parseInt(tvShowCount.getText().toString());
            Log.i("count", count+ "");
            if (count == 0) {
                return;
            }
            recordCount(count);
            tvShowCount.setText("0");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.analyse:
                Intent intent = new Intent(MainActivity.this, AnalyseActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void recordCount(int count) {
        final Record record = new Record();
        record.times = count;
        Date now = new Date();
        record.date = DateUtil.getDate(now);
        record.time = DateUtil.getTime(now);
        record.sync = false;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                db.recordDAO().insertRecord(record);
                countData();
            }

        });
        t.start();

    }

    private String getName(int id) {
        Resources res = getResources();
        return res.getResourceEntryName(id);//得到的是 name
    }

    private void countData(){
        new Thread(){
            @Override
            public void run() {
                int max = db.recordDAO().queryMax();
                String today = DateUtil.getDate(new Date());
                int todayMax = db.recordDAO().queryMaxByDate(today);
                int todayCount = db.recordDAO().querySumOfDateAboveMin(10, today);
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt(MAX, max);
                bundle.putInt(TODAY_MAX, todayMax);
                bundle.putInt(TODAY_COUNT, todayCount);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }.start();

    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int max = bundle.getInt(MAX);
            int todayMax = bundle.getInt(TODAY_MAX);
            int todayCount = bundle.getInt(TODAY_COUNT);
            tvMax.setText(String.valueOf(max));
            tvTodayMax.setText(String.valueOf(todayMax));
            tvTodayCount.setText(String.valueOf(todayCount));
        }
    };
}
