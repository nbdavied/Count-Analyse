package com.dw.countanalyse.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TimesCount {
    @PrimaryKey
    public int times;
    public int count;
}
