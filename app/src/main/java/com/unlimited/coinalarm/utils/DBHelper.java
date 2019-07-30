package com.unlimited.coinalarm.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String T_AlarmInfo =
            "create table T_AlarmInfo("
                    + "cc_id varchar,"//id
                    + "price double,"//告警价格
                    + "change_rate double,"//告警范围
                    + "duration integer,"//持续时间
                    + "image_id integer,"//图片id
                    + "is_on smallint)";//是否打开

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(T_AlarmInfo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists T_AlarmInfo");
        db.execSQL(T_AlarmInfo);
    }
}
