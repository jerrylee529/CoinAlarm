package com.unlimited.coinalarm.data;

import android.app.Application;

import java.util.Map;

public class ApplicationData extends Application {
    private AlarmSetting alarmSetting;

    public AlarmSetting getAlarmSetting() {
        return alarmSetting;
    }

    public void setAlarmSetting(AlarmSetting alarmSetting) {
        this.alarmSetting = alarmSetting;
    }

    public AlarmSetting.AlarmItem getAlarmItem(String instrumentId) {
        return alarmSetting.getList().get(instrumentId);
    }

    public Map<String, AlarmSetting.AlarmItem> getList() {
        return alarmSetting.getList();
    }
}
