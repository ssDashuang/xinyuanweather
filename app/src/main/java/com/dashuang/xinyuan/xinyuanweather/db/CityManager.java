package com.dashuang.xinyuan.xinyuanweather.db;

import org.litepal.crud.DataSupport;

/**
 * 用户已选择的城市
 * Created by chx on 2017/6/4.
 */
public class CityManager extends DataSupport{
    private int id;
    private String countyName;
    private String weatherId;
    private long time;  //播报天气的时间
    private String timeInfo;
    private boolean report;

    public boolean getReport() {
        return report;
    }

    public void setReport(boolean report) {
        this.report = report;
    }

    public String getTimeInfo() {
        return timeInfo;
    }

    public void setTimeInfo(String timeInfo) {
        this.timeInfo = timeInfo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }


    @Override
    public String toString() {
        return "CityManager{" +
                "id=" + id +
                ", countyName='" + countyName + '\'' +
                ", weatherId='" + weatherId + '\'' +
                ", time=" + time +
                ", timeInfo='" + timeInfo + '\'' +
                ", isReport=" + report +
                '}';
    }
}
