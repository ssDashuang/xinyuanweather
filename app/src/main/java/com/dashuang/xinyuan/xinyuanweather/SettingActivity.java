package com.dashuang.xinyuan.xinyuanweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout settingCurrentCity;

    private RelativeLayout settingAddCity;

    private RelativeLayout settingDelteCity;

    private Switch switchAutoUpdate;

    private RelativeLayout settingIntervalTime;

    private Switch switchVoiceReport;

    private RelativeLayout settingReportTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initEvent();
    }

    private void initEvent() {
        settingCurrentCity.setOnClickListener(this);
        settingAddCity.setOnClickListener(this);
        settingDelteCity.setOnClickListener(this);
        settingReportTime.setOnClickListener(this);
        settingIntervalTime.setOnClickListener(this);
    }

    private void initView() {
        settingAddCity = (RelativeLayout) findViewById(R.id.setting_add_city);
        settingCurrentCity = (RelativeLayout) findViewById(R.id.setting_current_city);
        settingDelteCity = (RelativeLayout) findViewById(R.id.setting_delete_city);
        settingIntervalTime = (RelativeLayout) findViewById(R.id.setting_interval_time);
        settingReportTime = (RelativeLayout) findViewById(R.id.setting_report_time);
        switchAutoUpdate = (Switch) findViewById(R.id.switch_auto_update);
        switchVoiceReport = (Switch) findViewById(R.id.switch_voice_report);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.setting_current_city:
                break;
            case R.id.setting_add_city:
                SettingDetailActivity.startAction(SettingActivity.this,
                        SettingDetailActivity.addCity);
                break;
            case R.id.setting_delete_city:
                break;
            case R.id.setting_interval_time:
                break;
            case R.id.setting_report_time:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingActivity.this,WeatherActivity.class);
        startActivity(intent);
    }
}
