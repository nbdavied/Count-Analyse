package com.dw.countanalyse;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dw.countanalyse.dao.RecordDAO;
import com.dw.countanalyse.entity.Record;

@Database(entities = {Record.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecordDAO recordDAO();
}
