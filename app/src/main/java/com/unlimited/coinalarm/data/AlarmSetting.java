package com.unlimited.coinalarm.data;

import java.util.Map;

public class AlarmSetting {
    private Map<String, AlarmItem> list;

    public Map<String, AlarmItem> getList() {
        return list;
    }

    public void setList(Map<String, AlarmItem> list) {
        this.list = list;
    }

    public AlarmItem newAlarmItem() {
        return new AlarmItem();
    }

    public int size() {
        return  (list!=null)?list.size():0;
    }

    public class AlarmItem {
        private String cc_id; // 数字币标识
        private String name; // 数字币名称
        private Double price; // 告警价格
        private Integer duration; // 价格持续时间, 单位：秒， 备用
        private Integer imageId; // 图片id
        private Boolean isOn; // 是否打开告警

        public AlarmItem() {
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
    }
}
