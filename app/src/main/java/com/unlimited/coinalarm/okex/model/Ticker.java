package com.unlimited.coinalarm.okex.model;

public class Ticker {
    private String instrument_id;
    private String last;
    private String best_bid;
    private String best_ask;
    private String open_24h;
    private String high_24h;
    private String low_24h;
    private String base_volume_24h;
    private String quote_volume_24h;
    private String timestamp;

    public String getInstrument_id() {
        return instrument_id;
    }

    public void setInstrument_id(String instrument_id) {
        this.instrument_id = instrument_id;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getBest_bid() {
        return best_bid;
    }

    public void setBest_bid(String best_bid) {
        this.best_bid = best_bid;
    }

    public String getBest_ask() {
        return best_ask;
    }

    public void setBest_ask(String best_ask) {
        this.best_ask = best_ask;
    }

    public String getOpen_24h() {
        return open_24h;
    }

    public void setOpen_24h(String open_24h) {
        this.open_24h = open_24h;
    }

    public String getHigh_24h() {
        return high_24h;
    }

    public void setHigh_24h(String high_24h) {
        this.high_24h = high_24h;
    }

    public String getLow_24h() {
        return low_24h;
    }

    public void setLow_24h(String low_24h) {
        this.low_24h = low_24h;
    }

    public String getBase_volume_24h() {
        return base_volume_24h;
    }

    public void setBase_volume_24h(String base_volume_24h) {
        this.base_volume_24h = base_volume_24h;
    }

    public String getQuote_volume_24h() {
        return quote_volume_24h;
    }

    public void setQuote_volume_24h(String quote_volume_24h) {
        this.quote_volume_24h = quote_volume_24h;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
