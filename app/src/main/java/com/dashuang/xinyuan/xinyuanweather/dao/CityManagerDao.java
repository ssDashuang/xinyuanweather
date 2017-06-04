package com.dashuang.xinyuan.xinyuanweather.dao;

import com.dashuang.xinyuan.xinyuanweather.db.CityManager;
import com.dashuang.xinyuan.xinyuanweather.db.County;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by chx on 2017/6/4.
 */

public class CityManagerDao {

    public static List<CityManager> queryAll(){
        List<CityManager> list = DataSupport.findAll(CityManager.class);
        return  list;
    }

    /**
     *添加城市
     * @return 是否添加成功
     */
    public static boolean addCity(County county){
        List<CityManager> list = queryAll();
        for (CityManager cityManager : list){
            if (cityManager.getWeatherId().equals(county.getWeatherId())){
                //如果数据库存在该数据，不添加
                return  false;
            }
        }
        CityManager manager = new CityManager();
        manager.setCountyName(county.getCountyName());
        manager.setWeatherId(county.getWeatherId());
        boolean result = manager.save();
        return  result;
    }

    public static void updateCity(String weatherId,County newCounty){
        CityManager manager = new CityManager();
        manager.setCountyName(newCounty.getCountyName());
        manager.setWeatherId(newCounty.getWeatherId());
        manager.updateAll("weatherId = ?",weatherId);
    }

    public static void deleteCity(String countyName){
        DataSupport.deleteAll(CityManager.class,"countyName = ?",countyName);
    }
}
