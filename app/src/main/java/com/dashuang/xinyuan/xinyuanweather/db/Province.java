package com.dashuang.xinyuan.xinyuanweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by chx on 2017/5/30.
 * 省份实体类，映射对应数据库中的省表
 */
public class Province extends DataSupport {

    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
