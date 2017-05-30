package com.dashuang.xinyuan.xinyuanweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chx on 2017/5/30.
 */

public class Now {
    //有的json字段命名不适合java字段命名，通过注解的方式让JSON字段和java字段建立映射关系
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
