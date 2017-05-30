package com.dashuang.xinyuan.xinyuanweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chx on 2017/5/30.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
