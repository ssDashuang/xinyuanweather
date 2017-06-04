package com.dashuang.xinyuan.xinyuanweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dashuang.xinyuan.xinyuanweather.dao.CityManagerDao;
import com.dashuang.xinyuan.xinyuanweather.db.CityManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<CityManager> list = CityManagerDao.queryAll();
     if (list.size() > 0){
            //如果有选择好的城市直接进入,数据库缓存
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
