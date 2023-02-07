package com.simonorow.zoomscheduler.Models;

public class TableTime {

    private String time = null;
    private String count = null;

    public TableTime() {
    }

    public TableTime(String time, String count) {
        this.time = time;
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

