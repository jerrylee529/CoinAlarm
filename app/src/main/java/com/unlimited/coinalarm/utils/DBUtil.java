package com.unlimited.coinalarm.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {
    public static final String TABLE_NAME = "T_AlarmInfo";

    public static SQLiteDatabase db(Context context) {
        return new DBHelper(context, "alarminfo.db", null, 3).getWritableDatabase();
    }
}
