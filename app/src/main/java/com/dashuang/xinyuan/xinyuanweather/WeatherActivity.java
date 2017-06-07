package com.dashuang.xinyuan.xinyuanweather;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dashuang.xinyuan.xinyuanweather.adapter.MyPagerAdapter;
import com.dashuang.xinyuan.xinyuanweather.dao.CityManagerDao;
import com.dashuang.xinyuan.xinyuanweather.db.CityManager;
import com.dashuang.xinyuan.xinyuanweather.fragment.WeatherFragment;
import com.dashuang.xinyuan.xinyuanweather.service.WeatherService;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";

    private DrawerLayout drawerLayout;
    private ViewPager vpContainer;
    private List<Fragment> fragments;
    private MyPagerAdapter fragmentAdapter;
    private List<CityManager> list;

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
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Intent intent = new Intent(WeatherActivity.this, WeatherService.class);
        startService(intent);
        initData();

    }
    public void openDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public void closeDrawer(){
        drawerLayout.closeDrawers();
    }
    public WeatherFragment getCurrentFragment(){
        int pos = vpContainer.getCurrentItem();
        return (WeatherFragment) fragments.get(pos);
    }
    private void initData() {
        fragments = new ArrayList<Fragment>();
            //查找数据库
        list = CityManagerDao.queryAll();
        if (list != null){
            for (CityManager cityManager : list){
                fragments.add(WeatherFragment.newInstance(cityManager.getWeatherId()));
            }
        }else {
                Intent intent = getIntent();
                String weatherId = intent.getStringExtra("weather_id");
                if (weatherId != null) {
                    Fragment fragment = WeatherFragment.newInstance(weatherId);
                    fragments.add(fragment);
                }
        }
        fragmentAdapter = new MyPagerAdapter(getSupportFragmentManager(),fragments);
        vpContainer.setAdapter(fragmentAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(WeatherActivity.this,WeatherService.class);
        stopService(intent);
    }
}
