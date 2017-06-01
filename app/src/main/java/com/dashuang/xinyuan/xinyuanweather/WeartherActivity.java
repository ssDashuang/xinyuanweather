package com.dashuang.xinyuan.xinyuanweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
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
import com.dashuang.xinyuan.xinyuanweather.gson.Forecast;
import com.dashuang.xinyuan.xinyuanweather.gson.Weather;
import com.dashuang.xinyuan.xinyuanweather.util.HttpUtil;
import com.dashuang.xinyuan.xinyuanweather.util.JsonUtility;
import com.dashuang.xinyuan.xinyuanweather.util.PrefUtil;
import com.dashuang.xinyuan.xinyuanweather.util.ToastUtil;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeartherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";

    private ScrollView scrollViewWeather;

    private TextView tvTitleCity;

    private TextView tvUpdateTime;

    private TextView tvDegree;

    private TextView tvWeatherInfo;

    private TextView tvAqi;

    private TextView tvPm25;

    private TextView tvComfort;

    private TextView tvCarWash;

    private TextView tvSport;

    private ImageView ivBg;

    private SwipeRefreshLayout refreshLayout;

    private LinearLayout layoutForecast;

    private String currentWeatherID ;
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
        //初始化各控件
        initView();
        //有缓存的情况使用缓存
        loadPref();
        //设置监听事件
        initEvent();
    }

    private void initEvent() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(currentWeatherID);
            }
        });
    }

    private void loadPref() {
        //加载天气缓存
        String weatherString = PrefUtil.getString(this, PrefConstantKey.WEATHER_WEATHER_INFO);
        if (weatherString != null){
            Weather weather = JsonUtility.parseWeatherJson(weatherString);
            currentWeatherID = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else {
            //无缓存时去服务查询天气
            String weatherID = getIntent().getStringExtra("weather_id");
            scrollViewWeather.setVisibility(View.INVISIBLE);
            currentWeatherID = weatherID;
            requestWeather(weatherID);
        }
        //加载图片缓存
        String bgPic = PrefUtil.getString(this,PrefConstantKey.WEATHER_BG);
        if (bgPic != null){
            Glide.with(this).load(bgPic).into(ivBg);
        }else{
            requestImage();
        }
    }


    private void initView() {
        scrollViewWeather = (ScrollView) findViewById(R.id.scrollview_weather);
        layoutForecast = (LinearLayout) findViewById(R.id.forecast_layout);
        tvAqi = (TextView) findViewById(R.id.tv_aqi);
        tvCarWash = (TextView) findViewById(R.id.tv_car_wash);
        tvComfort = (TextView) findViewById(R.id.tv_comfort);
        tvDegree = (TextView) findViewById(R.id.tv_degree_txt);
        tvPm25 = (TextView) findViewById(R.id.tv_pm25);
        tvTitleCity = (TextView) findViewById(R.id.tv_title_city);
        tvUpdateTime = (TextView) findViewById(R.id.tv_title_update_time);
        tvSport = (TextView) findViewById(R.id.tv_sport);
        tvWeatherInfo = (TextView) findViewById(R.id.tv_weather_info);
        ivBg = (ImageView) findViewById(R.id.iv_pic_bg);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_weather_refresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }


    private void requestImage() {
        HttpUtil.sendOkHttpRequest(ConstantURL.BG_IMG_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //访问失败
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(WeartherActivity.this,"网络繁忙");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bgPic = response.body().string();
                //缓存到本地
                PrefUtil.putString(WeartherActivity.this,PrefConstantKey.WEATHER_BG,bgPic);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeartherActivity.this).load(bgPic).into(ivBg);
                    }
                });
            }
        });
    }

    private void requestWeather(final String weatherID){
        //拼接URL
        String url = ConstantURL.WEATHER_URL+weatherID+ConstantURL.WEATHER_API_KEY;
        Log.e(TAG, "requestWeather: ------------->"+url );
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //访问失败
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(WeartherActivity.this,"网络繁忙");
                        refreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e(TAG, "onResponse: "+responseText );
                final Weather weather = JsonUtility.parseWeatherJson(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            PrefUtil.putString(WeartherActivity.this,
                                    PrefConstantKey.WEATHER_WEATHER_INFO,responseText);
                            showWeatherInfo(weather);
                        }else {
                            ToastUtil.show(WeartherActivity.this,"获取天气信息失败");
                        }
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 展示天气
     * @param weather 天气数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        tvTitleCity.setText(cityName);
        tvUpdateTime.setText(updateTime);
        tvWeatherInfo.setText(weatherInfo);
        tvDegree.setText(degree);
        layoutForecast.removeAllViews();
        for (Forecast f : weather.forecastList){
            View view = View.inflate(this,R.layout.forecast_item,null);
            TextView tvDate = (TextView) view.findViewById(R.id.tv_forecast_date);
            TextView tvInfo = (TextView) view.findViewById(R.id.tv_forecast_info);
            TextView tvMax = (TextView) view.findViewById(R.id.tv_forecast_max);
            TextView tvMin = (TextView) view.findViewById(R.id.tv_forecast_min);
            tvDate.setText(f.date);
            tvInfo.setText(f.more.info);
            tvMax.setText(f.temperature.max);
            tvMin.setText(f.temperature.min);
            layoutForecast.addView(view);
        }
        if (weather.aqi != null){
            Log.e(TAG, "showWeatherInfo:----------> "+weather.aqi.city.aqi );
            tvAqi.setText(weather.aqi.city.aqi);
            tvPm25.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度:" + weather.suggestion.comfort.info;
        String carWash = "洗车建议:" + weather.suggestion.carWash.info;
        String sport = "运动建议:" + weather.suggestion.sport.info;
        tvComfort.setText(comfort);
        tvCarWash.setText(carWash);
        tvSport.setText(sport);
        scrollViewWeather.setVisibility(View.VISIBLE);
    }
}
