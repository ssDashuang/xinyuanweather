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
}
