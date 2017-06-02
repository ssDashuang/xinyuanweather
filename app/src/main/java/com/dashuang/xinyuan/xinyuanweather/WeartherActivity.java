package com.dashuang.xinyuan.xinyuanweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dashuang.xinyuan.xinyuanweather.Global.ConstantURL;
import com.dashuang.xinyuan.xinyuanweather.Global.PrefConstantKey;
import com.dashuang.xinyuan.xinyuanweather.adapter.MyPagerAdapter;
import com.dashuang.xinyuan.xinyuanweather.fragment.WeatherFragment;
import com.dashuang.xinyuan.xinyuanweather.gson.Forecast;
import com.dashuang.xinyuan.xinyuanweather.gson.Weather;
import com.dashuang.xinyuan.xinyuanweather.util.HttpUtil;
import com.dashuang.xinyuan.xinyuanweather.util.JsonUtility;
import com.dashuang.xinyuan.xinyuanweather.util.PrefUtil;
import com.dashuang.xinyuan.xinyuanweather.util.ToastUtil;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeartherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";

    private ViewPager vpContainer;
    private List<Fragment> fragments;
    private Set<String> weatherIdSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //根据Android版本调整布局
        if (Build.VERSION.SDK_INT >= 21){
            //获取本Activity的DecorView(装饰视图)
            View decorVeiw = getWindow().getDecorView();
            //改变UI布局，显示在状态栏上
            decorVeiw.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置状态栏为透明状态
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_wearther);
        vpContainer = (ViewPager) findViewById(R.id.vp_container);
        initData();

    }
    private void initData() {
        fragments = new ArrayList<Fragment>();
        weatherIdSet = PrefUtil.getSetString(this,PrefConstantKey.WEATHERID_SET);
        if(weatherIdSet != null){
            Fragment fragment;
            for (String weatherId : weatherIdSet){
                fragment = WeatherFragment.newInstance(weatherId);
                fragments.add(fragment);
            }
        }else {
            Intent intent = getIntent();
            String weatherId = intent.getStringExtra("weather_id");
            Fragment fragment = WeatherFragment.newInstance(weatherId);
            fragments.add(fragment);
        }
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(),fragments);
        vpContainer.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    public void updateWeatherId(){
        weatherIdSet = PrefUtil.getSetString(this,PrefConstantKey.WEATHERID_SET);
        if(weatherIdSet != null) {
            Fragment fragment;
            for (String weatherId : weatherIdSet) {
                fragment = WeatherFragment.newInstance(weatherId);
                fragments.add(fragment);
            }
        }
    }
}
