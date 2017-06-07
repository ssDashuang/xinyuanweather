package com.dashuang.xinyuan.xinyuanweather;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.dashuang.xinyuan.xinyuanweather.Global.PrefConstantKey;
import com.dashuang.xinyuan.xinyuanweather.util.PrefUtil;

import java.util.Set;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout settingCurrentCity;

    private TextView tvCurrentCity;

    private RelativeLayout settingAddCity;

    private RelativeLayout settingDeleteCity;

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
        settingDeleteCity.setOnClickListener(this);
        settingReportTime.setOnClickListener(this);
        settingIntervalTime.setOnClickListener(this);
        switchAutoUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PrefUtil.putBoolean(SettingActivity.this,PrefConstantKey.AUTO_UPDATE,b);
            }
        });
        switchVoiceReport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PrefUtil.putBoolean(SettingActivity.this,PrefConstantKey.VOICE_REPORT,b);
            }
        });
    }

    private void initView() {
        settingAddCity = (RelativeLayout) findViewById(R.id.setting_add_city);
        settingCurrentCity = (RelativeLayout) findViewById(R.id.setting_current_city);
        settingDeleteCity = (RelativeLayout) findViewById(R.id.setting_delete_city);
        settingIntervalTime = (RelativeLayout) findViewById(R.id.setting_interval_time);
        settingReportTime = (RelativeLayout) findViewById(R.id.setting_report_time);
        switchAutoUpdate = (Switch) findViewById(R.id.switch_auto_update);
        switchVoiceReport = (Switch) findViewById(R.id.switch_voice_report);
        tvCurrentCity = (TextView) findViewById(R.id.tv_setting_current_position);

        String currentCity = PrefUtil.getString(SettingActivity.this,
                PrefConstantKey.CURRENT_LOCATION);
        if (currentCity != null) {
            String[] str = currentCity.split(",");
            tvCurrentCity.setText(str[str.length - 1]);
        }

        boolean autoUpdate = PrefUtil.getBoolean(SettingActivity.this,PrefConstantKey.AUTO_UPDATE);
        boolean voiceReport = PrefUtil.getBoolean(SettingActivity.this,PrefConstantKey.VOICE_REPORT);
        switchAutoUpdate.setChecked(autoUpdate);
        switchVoiceReport.setChecked(voiceReport);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.setting_current_city:
                showDialog();
                break;
            case R.id.setting_add_city:
                SettingDetailActivity.startAction(SettingActivity.this,
                        SettingDetailActivity.ADD_CITY);
                break;
            case R.id.setting_delete_city:
                SettingDetailActivity.startAction(SettingActivity.this,
                        SettingDetailActivity.DELETE_CITY);
                break;
            case R.id.setting_interval_time:
                SettingDetailActivity.startAction(SettingActivity.this,
                        SettingDetailActivity.INTERVAL_TIME);
                break;
            case R.id.setting_report_time:
                SettingDetailActivity.startAction(SettingActivity.this,
                        SettingDetailActivity.SETTING_REPORT);
                break;
        }
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setCancelable(false);
        builder.setTitle("更新位置");
        builder.setMessage("是否更新当前位置，并添加预报");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                intent.putExtra("update_location",true);
                startActivity(intent);
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingActivity.this,WeatherActivity.class);
        startActivity(intent);
        this.finish();
    }
}
