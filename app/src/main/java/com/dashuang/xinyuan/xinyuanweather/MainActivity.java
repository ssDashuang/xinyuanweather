package com.dashuang.xinyuan.xinyuanweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.dashuang.xinyuan.xinyuanweather.gson.Weather;
import com.dashuang.xinyuan.xinyuanweather.util.PrefUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String weatherString = PrefUtil.getString(this,WeartherActivity.PREF_KEY);
        if (!TextUtils.isEmpty(weatherString)){
            Intent intent = new Intent(this,WeartherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
