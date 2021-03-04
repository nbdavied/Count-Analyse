package com.dw.countanalyse.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Record {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int times;
    public String date;
    public String time;
    public boolean sync;
}
