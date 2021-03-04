package com.dw.countanalyse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dw.countanalyse.entity.Record;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvShowCount;
    AppDatabase db;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;
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
        tvShowCount = findViewById(R.id.tvShowCount);
        tvShowCount.setOnClickListener(this);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "record-db").build();

        dateFormat = new SimpleDateFormat("yyyyMMdd");
        timeFormat = new SimpleDateFormat("HHmmss");
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
            if (count == 0) {
                return;
            }
            recordCount(count);
            tvShowCount.setText("0");
        }
    }

    private void recordCount(int count) {
        Record record = new Record();
        record.times = count;
        Date now = new Date();
        record.date = dateFormat.format(now);
        record.time = timeFormat.format(now);
        record.sync = false;
        db.recordDAO().insertRecord(record);
    }

    private String getName(int id) {
        Resources res = getResources();
        return res.getResourceEntryName(id);//得到的是 name
    }
}
