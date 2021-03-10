package com.dw.countanalyse.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class DailyStatistic {
    @PrimaryKey
    public String date;
    public float avg;
    public int countTimes;
    public int max;

}
