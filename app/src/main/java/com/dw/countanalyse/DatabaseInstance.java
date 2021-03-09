package com.dw.countanalyse;

import androidx.room.Room;

public class DatabaseInstance {
    private AppDatabase db;
    private DatabaseInstance(){
        db = Room.databaseBuilder(CountAnalyseApplication.getContext(),
                AppDatabase.class, "record-db").build();
    }
    private static DatabaseInstance instance;
    public static AppDatabase getDb(){
        if(instance == null){
            instance = new DatabaseInstance();
            return instance.db;
        }else{
            return instance.db;
        }
    }
}
