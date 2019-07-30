package com.unlimited.coinalarm.data;

import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.unlimited.coinalarm.R;
import com.unlimited.coinalarm.utils.DBUtil;

import java.util.HashMap;
import java.util.Map;

public class ApplicationData extends Application {
    private static final String TAG = "ApplicationData";

    private static final String[] cc_ids = {"BTC", "ETH", "LTC"}; // cryptocurrency id

    private static final Map<String, String> map_cc_id;

    static {
        map_cc_id = new HashMap<>();
        map_cc_id.put("BTC", "BTC");
        map_cc_id.put("ETH", "ETH");
        map_cc_id.put("LTC", "LTC");
        //map_cc_id.put("EOS", "EOS");
    }

    private static final Map<String, Integer> map_image_id;

    static {
        map_image_id = new HashMap<>();
        map_image_id.put("BTC", R.drawable.bitcoin_48_48);
        map_image_id.put("ETH", R.drawable.ethereum_48_48);
        map_image_id.put("LTC", R.drawable.litecoin_mid_48);
        //map_image_id.put("EOS", R.drawable.tether_48_48);
    }

    private Map<String, AlarmItem> list; // 告警币种的列表

    public ApplicationData() {
        super();
        list = new HashMap<>();
    }

    public Map<String, AlarmItem> getList() {
        return list;
    }

    /*
    public void setAlarmSetting(AlarmSetting alarmSetting) {
        this.alarmSetting = alarmSetting;
    }
    */
    public Integer getInterval() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        return settings.getInt("interval", 60);
    }

    public void setInterval(Integer interval) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        settings.edit().putInt("interval", interval).apply();
    }

    public Boolean getOn() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        return settings.getBoolean("isOn", false);
    }

    public void setOn(Boolean on) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        settings.edit().putBoolean("isOn", on).apply();
    }

    public AlarmItem getAlarmItem(String instrumentId) {
        return getList().get(instrumentId);
    }

    public Map<String, AlarmItem> saveItems() {
        for (AlarmItem item: getList().values()) {
            Cursor cursor = DBUtil.db(this).query(DBUtil.TABLE_NAME, new String[]{"cc_id", "price", "duration", "image_id", "is_on", "change_rate"}, "cc_id=?", new String[]{item.getCc_id()}, null, null, null);
            if (cursor.moveToNext()) {
                ContentValues values = new ContentValues();
                values.put("cc_id", item.getCc_id());
                values.put("price", item.getPrice());
                values.put("duration", item.getDuration());
                values.put("image_id", item.getImageId());
                values.put("is_on", item.getOn()?1:0);
                values.put("change_rate", item.getChangeRate());
                SQLiteDatabase db = DBUtil.db(this);
                db.update(DBUtil.TABLE_NAME, values,"cc_id = ?", new String[]{item.getCc_id()});
            } else {
                ContentValues values = new ContentValues();
                values.put("cc_id", item.getCc_id());
                values.put("price", item.getPrice());
                values.put("duration", item.getDuration());
                values.put("image_id", item.getImageId());
                values.put("is_on", item.getOn()?1:0);
                values.put("change_rate", item.getDuration());
                SQLiteDatabase db = DBUtil.db(this);
                db.insert(DBUtil.TABLE_NAME, null, values);
            }
        }

        return list;
    }

    public Map<String, AlarmItem> loadItems() {
        SQLiteDatabase db = DBUtil.db(this);

        //db.execSQL("delete from " + DBUtil.TABLE_NAME + ";");

        Cursor cursor = db.query(DBUtil.TABLE_NAME, new String[]{"cc_id", "price", "duration", "image_id", "is_on", "change_rate"}, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Log.d(TAG, "cc_id--->" + cursor.getString(0));
                Log.d(TAG, "price--->" + cursor.getString(1));
                Log.d(TAG, "duration--->" + cursor.getString(2));
                Log.d(TAG, "image_id--->" + cursor.getInt(3));
                Log.d(TAG, "is_on--->" + cursor.getString(4));
                Log.d(TAG, "change_rate--->" + cursor.getString(5));
                AlarmItem alarmItem = newAlarmItem();
                alarmItem.setCc_id(cursor.getString(0));
                alarmItem.setPrice(cursor.getDouble(1));
                alarmItem.setDuration(cursor.getInt(2));
                alarmItem.setImageId(cursor.getInt(3));
                alarmItem.setOn((cursor.getShort(4)==1)?Boolean.TRUE:Boolean.FALSE);
                alarmItem.setChangeRate(cursor.getDouble(5));
                list.put(cursor.getString(0), alarmItem);
            }
        } else {
            loadDefault();
            saveItems();
        }

        cursor.close();

        return list;
    }

    public AlarmItem newAlarmItem() {
        return new AlarmItem();
    }

    public class AlarmItem {
        private String cc_id; // 数字币标识
        private String name; // 数字币名称
        private Double price; // 告警价格
        private Integer duration; // 价格持续时间, 单位：秒， 备用
        private Integer imageId; // 图片id
        private Boolean isOn; // 是否打开告警
        private Double changeRate; // 告警范围

        public AlarmItem() {
            cc_id = "";
            name = "";
            price = 0.00;
            duration = 5;
            imageId = 0;
            isOn = Boolean.FALSE;
        }

        public String getCc_id() {
            return cc_id;
        }

        public void setCc_id(String cc_id) {
            this.cc_id = cc_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public Integer getImageId() {
            return imageId;
        }

        public void setImageId(Integer imageId) {
            this.imageId = imageId;
        }

        public Boolean getOn() {
            return isOn;
        }

        public void setOn(Boolean on) {
            isOn = on;
        }

        public Double getChangeRate() {
            return changeRate;
        }

        public void setChangeRate(Double changeRate) {
            this.changeRate = changeRate;
        }
    }

    private void loadDefault() {
        for (int i = 0; i < cc_ids.length; i++) {
            AlarmItem alarmItem = newAlarmItem();
            alarmItem.setCc_id(cc_ids[i]);
            alarmItem.setName(cc_ids[i]);
            alarmItem.setImageId(map_image_id.get(cc_ids[i]));
            alarmItem.setDuration(0);
            alarmItem.setChangeRate(0.0);
            list.put(cc_ids[i], alarmItem);
        }
    }
}
