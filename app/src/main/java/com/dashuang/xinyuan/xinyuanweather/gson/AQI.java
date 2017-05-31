package com.dashuang.xinyuan.xinyuanweather.gson;

/**
 * Created by chx on 2017/5/30.
 */

public class AQI {

    public AQICity city;

    public class AQICity{

        public String aqi;

        public String pm10;

        public String pm25;

        public String qlty;
    }
}
