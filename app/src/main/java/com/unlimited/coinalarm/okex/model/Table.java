package com.unlimited.coinalarm.okex.model;

import java.util.List;

public class Table {
    private String table;
    private List<Ticker> data;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<Ticker> getData() {
        return data;
    }

    public void setData(List<Ticker> data) {
        this.data = data;
    }
}
