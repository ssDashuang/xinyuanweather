package com.dashuang.xinyuan.xinyuanweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class WeartherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView tvTitleCity;

    private TextView tvUpdateTime;

    private TextView tvDegree;

    private TextView tvWeatherInfo;

    private TextView tvAqi;

    private TextView tvPm25;

    private TextView tvComfort;

    private TextView tvCarWash;

    private TextView tvSport;

    private LinearLayout layoutForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearther);
        //初始化各控件

    }
}
