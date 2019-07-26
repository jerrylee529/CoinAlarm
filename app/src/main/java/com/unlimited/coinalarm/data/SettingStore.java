package com.unlimited.coinalarm.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.unlimited.coinalarm.utils.DBUtil;

import java.util.HashMap;
import java.util.Map;

public class SettingStore {
    private Context context;

    private static final String TAG = "SettingStore";

    private static SettingStore s_SettingStore = null;

    private SettingStore(Context context) {
        this.context = context;
    }

    public static SettingStore getInstance(Context context) {
        if (s_SettingStore == null) {
            s_SettingStore = new SettingStore(context);
        }

        return s_SettingStore;
    }

    public void save(AlarmSetting alarmSetting) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("coinalarm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        StringBuffer instruments = new StringBuffer();
        StringBuffer prices = new StringBuffer();
        StringBuffer durations = new StringBuffer();
        for (AlarmSetting.AlarmItem item: alarmSetting.getList().values()) {
            instruments.append(item.getCc_id());
            instruments.append(",");
            prices.append(item.getPrice());
            prices.append(",");
            durations.append(item.getDuration());
            durations.append(",");
        }
        editor.putString("instruments", instruments.toString());
        editor.putString("prices", prices.toString());
        editor.putString("durations", durations.toString());
        editor.commit();
    }

    public AlarmSetting load() {
        SharedPreferences preferences = context.getSharedPreferences("coinalarm", Context.MODE_PRIVATE);
        AlarmSetting alarmSetting = new AlarmSetting();

        String instruments = preferences.getString("instruments", "");
        String prices = preferences.getString("prices", "");
        String durations = preferences.getString("durations", "");

        return alarmSetting;
    }

    public void save2DB(AlarmSetting alarmSetting) {
        for (AlarmSetting.AlarmItem item: alarmSetting.getList().values()) {
            Cursor cursor = DBUtil.db(context).query(DBUtil.TABLE_NAME, new String[]{"cc_id", "price", "duration", "image_id", "is_on"}, "cc_id=?", new String[]{item.getCc_id()}, null, null, null);
            if (cursor.moveToNext()) {
                ContentValues values = new ContentValues();
                values.put("cc_id", item.getCc_id());
                values.put("price", item.getPrice());
                values.put("duration", item.getDuration());
                values.put("image_id", item.getImageId());
                values.put("is_on", item.getOn()?1:0);
                SQLiteDatabase db = DBUtil.db(context);
                db.update(DBUtil.TABLE_NAME, values,"cc_id = ?", new String[]{item.getCc_id()});
            } else {
                ContentValues values = new ContentValues();
                values.put("cc_id", item.getCc_id());
                values.put("price", item.getPrice());
                values.put("duration", item.getDuration());
                values.put("image_id", item.getImageId());
                values.put("is_on", item.getOn()?1:0);
                SQLiteDatabase db = DBUtil.db(context);
                db.insert(DBUtil.TABLE_NAME, null, values);
            }
        }
    }

    public AlarmSetting loadFromDB() {
        AlarmSetting alarmSetting = new AlarmSetting();
        Map<String, AlarmSetting.AlarmItem> map = new HashMap<>();

        SQLiteDatabase db = DBUtil.db(context);

        //db.execSQL("delete from " + DBUtil.TABLE_NAME + ";");

        Cursor cursor = db.query(DBUtil.TABLE_NAME, new String[]{"cc_id", "price", "duration", "image_id", "is_on"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Log.d(TAG, "cc_id--->" + cursor.getString(0));
            Log.d(TAG, "price--->" + cursor.getString(1));
            Log.d(TAG, "duration--->" + cursor.getString(2));
            Log.d(TAG, "image_id--->" + cursor.getInt(3));
            Log.d(TAG, "is_on--->" + cursor.getString(4));
            AlarmSetting.AlarmItem alarmItem = alarmSetting.newAlarmItem();
            alarmItem.setCc_id(cursor.getString(0));
            alarmItem.setPrice(cursor.getDouble(1));
            alarmItem.setDuration(cursor.getInt(2));
            alarmItem.setImageId(cursor.getInt(3));
            alarmItem.setOn((cursor.getShort(4)==1)?Boolean.TRUE:Boolean.FALSE);
            map.put(cursor.getString(0), alarmItem);
        }

        alarmSetting.setList(map);

        return alarmSetting;
    }
}
