package com.dw.countanalyse.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dw.countanalyse.entity.Record;
import com.dw.countanalyse.entity.TimesCount;

import java.util.List;

@Dao
public interface RecordDAO {
    @Insert
    void insertRecord(Record record);

    @Query("select * from record where date between :startDate and :endDate")
    List<Record> queryRecordBetweenDate(String startDate, String endDate);

    @Query("select times, count(*) as count from record where date = :date group by times")
    List<TimesCount> queryTimesCount(String date);
}
